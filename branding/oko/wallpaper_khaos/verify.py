"""Reproduce the approved OKO wallpaper archive and copy its checked PNGs."""

from base64 import b64decode
import hashlib
from io import BytesIO
import os
from pathlib import Path
import re
import shutil
from struct import pack
import subprocess
import sys
from tempfile import TemporaryDirectory
from zipfile import ZipFile

import numpy as np
from PIL import Image, __version__ as pillow_version

BRAND = Path(__file__).resolve().parent
ROOT = "interatletika_khaos_chystyi"
ARCHIVE = f"{ROOT}_package.zip"
EMBEDDED_PNG = re.compile(rb"data:image/png;base64,([A-Za-z0-9+/=]+)")
MODES = (
    ("desktop", "3", "BASE_D", "310", 3840, 2160, "3840x2160"),
    ("phone", "4", "BASE_P", "215", 1290, 2796, "phone_1290x2796"),
)
REFERENCE_PACKAGES = {
    "librsvg2-bin": "2.58.0+dfsg-1ubuntu0.1",
    "libcairo2": "1.18.0-3build1",
    "libpango-1.0-0": "1.52.1+ds-1build1",
    "fontconfig": "2.15.0-1.1ubuntu2",
    "libfreetype6": "2.13.2+dfsg-1ubuntu0.2",
    "libharfbuzz0b": "8.3.0-2build2",
    "fonts-liberation-sans-narrow": "1:1.07.6-4",
}


def verify_hash(path, expected):
    with path.open("rb") as handle:
        actual = hashlib.file_digest(handle, "sha256").hexdigest()
    if actual != expected:
        raise SystemExit(f"SHA-256 mismatch for {path.name}: {actual}")
    print(f"SHA-256 OK: {actual}  {path.name}", flush=True)


def check_renderer():
    versions = dict(
        record.split("\t")
        for record in subprocess.check_output(
            ["dpkg-query", "-W", "-f=${Package}\\t${Version}\\n", *REFERENCE_PACKAGES],
            text=True,
        ).splitlines()
    )
    for name, version in versions.items():
        print(f"{name} {version}", flush=True)
    print(f"Python {sys.version.split()[0]} | Pillow {pillow_version} | numpy {np.__version__}", flush=True)
    subprocess.run(["rsvg-convert", "--version"], check=True)
    font = subprocess.check_output(
        ["fc-match", "-f", "%{file}", "Liberation Sans Narrow:style=Italic"], text=True
    )
    print(f"Font: {font}", flush=True)
    if Path(font).name != "LiberationSansNarrow-Italic.ttf":
        raise SystemExit("Liberation Sans Narrow Italic was not selected")
    if versions != REFERENCE_PACKAGES:
        print("::notice::Ubuntu rasterizer packages differ from the reference versions.")


def svg_content(svg):
    def replace_png(match):
        with Image.open(BytesIO(b64decode(match[1], validate=True)), formats=["PNG"]) as image:
            pixels = image.convert("RGBA")
            digest = hashlib.sha256(pack(">II", *pixels.size) + pixels.tobytes()).hexdigest()
        return b"data:image/png;rgba-sha256," + digest.encode("ascii")

    return EMBEDDED_PNG.subn(replace_png, svg)


def reproduce(output):
    records = [line.split() for line in (BRAND / "SHA256SUMS").read_text().splitlines()]
    expected_files = {ARCHIVE, *(f"{ROOT}/etalon/{ROOT}_{mode[-1]}.png" for mode in MODES)}
    if len(records) != 3 or {name for _, name in records} != expected_files:
        raise SystemExit("SHA256SUMS must cover the archive and the two reference PNGs")
    manifest = {name: digest for digest, name in records}
    archive = BRAND / ARCHIVE
    verify_hash(archive, manifest[ARCHIVE])
    check_renderer()
    env = os.environ.copy()
    for key in ("ROTMAX", "BAR", "BASE_D", "BASE_P"):
        env.pop(key, None)
    env.update(NOTAG="1", NOTB="1")

    with TemporaryDirectory() as temporary:
        extracted = Path(temporary)
        with ZipFile(archive) as package:
            package.extractall(extracted)
        source = extracted / ROOT
        for name in sorted(expected_files - {ARCHIVE}):
            verify_hash(extracted / name, manifest[name])

        references = []
        pixel_mismatch = False
        for mode, seed, base_key, base, width, height, size in MODES:
            svg = source / f"art_{mode}.svg"
            reference_svg = source / "etalon" / f"khaos_chystyi_{mode}.svg"
            reference = source / "etalon" / f"{ROOT}_{size}.png"
            generated = source / f"khaos_{size}.png"
            subprocess.run(
                [sys.executable, "art.py", mode, seed],
                cwd=source,
                env={**env, base_key: base},
                check=True,
            )
            actual_svg = svg.read_bytes()
            expected_svg = reference_svg.read_bytes()
            actual_content, actual_count = svg_content(actual_svg)
            expected_content, expected_count = svg_content(expected_svg)
            if actual_count != expected_count or actual_content != expected_content:
                raise SystemExit(f"SVG text or embedded PNG pixels differ: {mode}")
            print(
                f"SVG content identical: {mode} ({actual_count} embedded PNGs); "
                f"identical bytes: {actual_svg == expected_svg}",
                flush=True,
            )
            subprocess.run(
                ["rsvg-convert", "-w", str(width), "-h", str(height), str(svg), "-o", str(generated)],
                check=True,
            )
            with Image.open(reference) as original, Image.open(generated) as reproduced:
                if original.size != (width, height) or reproduced.size != original.size:
                    raise SystemExit(f"Unexpected PNG dimensions: {mode}")
                difference = np.any(
                    np.asarray(original.convert("RGBA")) != np.asarray(reproduced.convert("RGBA")),
                    axis=2,
                )
                count = int(np.count_nonzero(difference))
            print(f"{mode}: різниця з еталоном: {count}", flush=True)
            pixel_mismatch |= count != 0
            references.append(reference)

        if pixel_mismatch:
            print(
                "::warning::Rasterized PNG pixels differ on this Ubuntu runner. "
                "The verified reference PNGs are retained because both SVGs match in content."
            )
        output.mkdir(parents=True, exist_ok=True)
        for reference in references:
            shutil.copyfile(reference, output / reference.name)


if __name__ == "__main__":
    reproduce(Path(sys.argv[1]))
