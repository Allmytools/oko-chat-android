"""Capture B5c on a fresh Android emulator using the packaged OKO test APK."""

from io import BytesIO
from pathlib import Path
import re
import subprocess
import sys
import time
from xml.etree import ElementTree

from PIL import Image


PACKAGE = "com.interatletika.oko.test"
LABEL = "OKO test"
WIDTH, HEIGHT = 540, 960


def adb(*args):
    return subprocess.check_output(["adb", *args], timeout=25)


def shell(*args):
    return adb("shell", *args).decode().strip()


def hierarchy():
    shell("uiautomator", "dump", "/sdcard/oko-window.xml")
    return ElementTree.fromstring(adb("exec-out", "cat", "/sdcard/oko-window.xml"))


def app_icon(*, workspace=False):
    for node in hierarchy().iter("node"):
        if LABEL in (node.get("text"), node.get("content-desc")):
            bounds = tuple(map(int, re.findall(r"\d+", node.attrib["bounds"])))
            # The launcher hotseat can suggest apps and replace them after a theme change.
            if not workspace or bounds[3] < HEIGHT * 0.73:
                return bounds
    return None


def wait_for_icon(*, workspace=False):
    for _ in range(4):
        bounds = app_icon(workspace=workspace)
        if bounds:
            return bounds
        time.sleep(1)
    raise RuntimeError("Launcher did not show the installed OKO test icon")


def home():
    shell("input", "keyevent", "KEYCODE_HOME")
    time.sleep(1.5)


def pin_icon():
    home()
    for _ in range(2):
        if app_icon(workspace=True):
            return
        shell("input", "swipe", "270", "820", "270", "310", "400")
        time.sleep(1.2)
        left, top, right, bottom = wait_for_icon()
        shell("input", "draganddrop", str((left + right) // 2), str((top + bottom) // 2), "270", "390", "1800")
        home()
    wait_for_icon(workspace=True)


def screenshot():
    return adb("exec-out", "screencap", "-p")


def launcher_capture(output):
    pin_icon()
    left, top, right, bottom = wait_for_icon(workspace=True)
    data = screenshot()
    output.write_bytes(data)
    with Image.open(BytesIO(data)) as image:
        return image.convert("RGB").crop((left, top, right, bottom)).tobytes()


def theme_icons(launcher, enabled):
    uri = "content://" + launcher + ".grid_control"
    shell("content", "update", "--uri", uri + "/set_icon_themed", "--bind", "boolean_value:b:" + str(enabled).lower())
    state = shell("content", "query", "--uri", uri + "/get_icon_themed")
    if "boolean_value=" + str(int(enabled)) not in state:
        raise RuntimeError("Launcher did not apply themed icons: " + state)
    time.sleep(2)


def app_screen(activity, output, appearance):
    splash = output / ("splash-" + appearance + ".png")
    for _ in range(4):
        home()
        shell("am", "force-stop", PACKAGE)
        # Start asynchronously so Android's transient system splash can be captured.
        with subprocess.Popen(
            ["adb", "shell", "am", "start", "--splashscreen-show-icon", "-n", activity],
            stdout=subprocess.DEVNULL,
        ) as start:
            deadline = time.monotonic() + 2.5
            while time.monotonic() < deadline:
                data = screenshot()
                with Image.open(BytesIO(data)) as frame:
                    rgb = frame.convert("RGB")
                    cx, cy = rgb.width // 2, rgb.height // 2
                    middle = rgb.crop((cx - 65, cy - 65, cx + 65, cy + 65))
                    green = sum(1 for r, g, b in middle.get_flattened_data() if r <= 7 and 121 <= g <= 135 and 97 <= b <= 113)
                    if green > 2300:
                        splash.write_bytes(data)
                        break
            if start.wait(timeout=12) != 0:
                raise RuntimeError("Could not start the packaged OKO activity")
        if splash.is_file():
            break
    if not splash.is_file():
        raise RuntimeError("No B5c system splash frame was captured in " + appearance + " mode")

    for _ in range(5):
        nodes = tuple(hierarchy().iter("node"))
        if any(
            node.get("package") == PACKAGE
            and (node.get("text") or "").lower().startswith(("sign in", "continue", "get started", "create account"))
            for node in nodes
        ):
            (output / ("onboarding-" + appearance + ".png")).write_bytes(screenshot())
            return
        time.sleep(1)
    raise RuntimeError("OKO onboarding was not visible")


def main():
    apk, path = sys.argv[1:]
    output = Path(path)
    output.mkdir(parents=True, exist_ok=True)
    shell("wm", "size", f"{WIDTH}x{HEIGHT}")
    shell("wm", "density", "220")
    shell("settings", "put", "system", "system_locales", "en-US")
    shell("input", "keyevent", "82")
    adb("install", apk)

    home()
    launchers = shell("cmd", "package", "resolve-activity", "--brief", "-a", "android.intent.action.MAIN", "-c", "android.intent.category.HOME")
    launcher = launchers.splitlines()[-1].split("/")[0]
    activities = shell("cmd", "package", "resolve-activity", "--brief", "-a", "android.intent.action.MAIN", "-c", "android.intent.category.LAUNCHER", PACKAGE)
    activity = activities.splitlines()[-1]
    if not activity.startswith(PACKAGE + "/") or not launcher.endswith(("launcher3", "nexuslauncher")):
        raise RuntimeError("Could not identify the OKO activity and Android system launcher")

    theme_icons(launcher, False)
    pin_icon()
    for appearance, mode in (("light", "no"), ("dark", "yes")):
        shell("cmd", "uimode", "night", mode)
        time.sleep(1)
        normal_icon = launcher_capture(output / ("launcher-" + appearance + ".png"))
        app_screen(activity, output, appearance)
        home()
        theme_icons(launcher, True)
        themed_icon = launcher_capture(output / ("launcher-themed-" + appearance + ".png"))
        if themed_icon == normal_icon:
            raise RuntimeError("The themed icon did not change on the launcher")
        theme_icons(launcher, False)


if __name__ == "__main__":
    main()
