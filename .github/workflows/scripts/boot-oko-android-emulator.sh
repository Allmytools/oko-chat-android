#!/usr/bin/env bash
set -euo pipefail

image='system-images;android-35;google_apis;x86_64'
sdk="$ANDROID_HOME/cmdline-tools/latest/bin"
"$sdk/sdkmanager" emulator "$image"
"$sdk/avdmanager" create avd --force --name oko-b5c --package "$image" <<< no
test -c /dev/kvm
sudo chmod a+rw /dev/kvm
"$ANDROID_HOME/emulator/emulator" -avd oko-b5c -no-window -no-audio -no-boot-anim \
    -no-snapshot -camera-back none -camera-front none -gpu swiftshader_indirect \
    > "$RUNNER_TEMP/oko-emulator.log" 2>&1 &
trap 'adb emu kill || true' EXIT

deadline=$((SECONDS + 180))
until [[ $(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r') == 1 ]]; do
    if (( SECONDS >= deadline )); then
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
