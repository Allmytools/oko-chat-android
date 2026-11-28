#!/usr/bin/env bash
set -euo pipefail

image='system-images;android-35;google_apis;x86_64'
sdk="$ANDROID_HOME/cmdline-tools/latest/bin"
export ANDROID_USER_HOME="$RUNNER_TEMP/oko-android-user"
export ANDROID_AVD_HOME="$ANDROID_USER_HOME/avd"
unset ANDROID_SDK_HOME
mkdir -p "$ANDROID_AVD_HOME"
"$sdk/sdkmanager" emulator "$image"
"$sdk/avdmanager" create avd --force --name oko-b5c --package "$image" \
    --device pixel_6 --path "$ANDROID_AVD_HOME/oko-b5c.avd"
test -f "$ANDROID_AVD_HOME/oko-b5c.ini"
"$ANDROID_HOME/emulator/emulator" -list-avds | grep -Fx oko-b5c
test -c /dev/kvm
sudo chmod a+rw /dev/kvm
"$ANDROID_HOME/emulator/emulator" -avd oko-b5c -no-window -no-audio -no-boot-anim \
    -no-snapshot -memory 1536 -camera-back none -camera-front none -gpu swiftshader_indirect \
    > "$RUNNER_TEMP/oko-emulator.log" 2>&1 &
emulator_pid=$!
trap 'adb emu kill || true' EXIT

deadline=$((SECONDS + 180))
until [[ $(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r') == 1 ]]; do
    if ! kill -0 "$emulator_pid" 2>/dev/null || (( SECONDS >= deadline )); then
        tail -80 "$RUNNER_TEMP/oko-emulator.log"
        exit 1
    fi
    sleep 3
done
adb root
adb wait-for-device
python3 -m venv "$RUNNER_TEMP/oko-emulator-python"
"$RUNNER_TEMP/oko-emulator-python/bin/pip" install --disable-pip-version-check 'Pillow==12.2.0'
"$RUNNER_TEMP/oko-emulator-python/bin/python" .github/workflows/scripts/capture-oko-android.py \
    "$RUNNER_TEMP/oko-apks/oko-test-universal.apk" "$RUNNER_TEMP/oko-android-b5c"


if [[ -d "$RUNNER_TEMP/oko-wallpaper-emulator-tests" ]]; then
    shopt -s nullglob
    test_apks=("$RUNNER_TEMP/oko-wallpaper-emulator-tests/"*.apk)
    [[ ${#test_apks[@]} -eq 1 ]]
    test_package=io.element.android.oko.wallpaper.test
    output="$RUNNER_TEMP/oko-android-b5c"
    adb install -r -t "${test_apks[0]}"
    adb shell pm grant "$test_package" android.permission.POST_NOTIFICATIONS
    adb shell head -n 1 /proc/meminfo
    adb shell am instrument -w -r "$test_package/androidx.test.runner.AndroidJUnitRunner" \
        | tee "$output/wallpaper-instrumentation.txt"
    grep -Eq 'OK \(2 tests\)' "$output/wallpaper-instrumentation.txt"
    for proof in low-memory-scroll-before.png low-memory-scroll-after.png notification-icon.png; do
        adb pull "/sdcard/Android/data/$test_package/files/$proof" "$output/$proof"
    done
    adb shell dumpsys gfxinfo "$test_package" framestats > "$output/low-memory-frames.txt"
fi
