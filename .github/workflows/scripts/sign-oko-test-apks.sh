#!/usr/bin/env bash
set -euo pipefail

if [[ "${OKO_BUILD_TEST:-}" != true ]]; then
    echo "::error::OKO test signing requires -Poko.test=true."
    exit 1
fi

tools="$ANDROID_HOME/build-tools/37.0.0"
unsigned_dir="$RUNNER_TEMP/oko-unsigned-apks"
output_dir="$RUNNER_TEMP/oko-apks"
abis=(universal arm64-v8a)

# Check the actual application ID of both APKs before loading any key or writing a signature.
for abi in "${abis[@]}"; do
    apk="$unsigned_dir/oko-test-$abi-unsigned.apk"
    badging=$("$tools/aapt" dump badging "$apk")
    if ! grep -Eq "^package: name='com\.interatletika\.oko\.test' " <<< "$badging"; then
        echo "::error::Refusing to sign $abi. Expected package com.interatletika.oko.test."
        exit 1
    fi
done

store_type=()
artifact=oko-test-apks
if [[ -z "${OKO_TEST_SIGNING_STORE_BASE64:-}${OKO_TEST_SIGNING_STORE_PASSWORD:-}${OKO_TEST_SIGNING_KEY_ALIAS:-}${OKO_TEST_SIGNING_KEY_PASSWORD:-}" ]]; then
    artifact=oko-test-apks-debugkey
    store=app/signature/debug.keystore
    debug_settings=$(python3 - <<'PY'
import re
from pathlib import Path

source = Path("app/build.gradle.kts").read_text(encoding="utf-8")
block = re.search(r'signingConfigs\s*\{\s*getByName\("debug"\)\s*\{([^}]+)\}', source)
if not block or not re.search(r'^\s*storeFile\s*=\s*file\("\./signature/debug\.keystore"\)\s*$', block[1], re.M):
    raise SystemExit("Expected the Element debug signing block and keystore path.")
for field in ("keyAlias", "storePassword", "keyPassword"):
    value = re.search(rf'^\s*{field}\s*=\s*"([^"\r\n]+)"\s*$', block[1], re.M)
    if not value:
        raise SystemExit(f"Missing {field} in the Element debug signing block.")
    print(value[1])
PY
    )
    mapfile -t debug_fields <<< "$debug_settings"
    key_alias="${debug_fields[0]}"
    export OKO_APK_STORE_PASSWORD="${debug_fields[1]}"
    export OKO_APK_KEY_PASSWORD="${debug_fields[2]}"
else
    : "${OKO_TEST_SIGNING_STORE_BASE64:?Missing test signing store}"
    : "${OKO_TEST_SIGNING_STORE_PASSWORD:?Missing test store password}"
    : "${OKO_TEST_SIGNING_KEY_ALIAS:?Missing test signing alias}"
    : "${OKO_TEST_SIGNING_KEY_PASSWORD:?Missing test key password}"
    umask 077
    store=$(mktemp)
    trap 'rm -f "$store"' EXIT
    printf '%s' "$OKO_TEST_SIGNING_STORE_BASE64" | base64 --decode > "$store"
    store_type=(--ks-type PKCS12)
    key_alias="$OKO_TEST_SIGNING_KEY_ALIAS"
    export OKO_APK_STORE_PASSWORD="$OKO_TEST_SIGNING_STORE_PASSWORD"
    export OKO_APK_KEY_PASSWORD="$OKO_TEST_SIGNING_KEY_PASSWORD"
fi

expected_sha=$(
    keytool -exportcert -keystore "$store" -alias "$key_alias" -storepass:env OKO_APK_STORE_PASSWORD |
        sha256sum | cut -d ' ' -f 1
)

mkdir -p "$output_dir"
for abi in "${abis[@]}"; do
    apk="$unsigned_dir/oko-test-$abi-unsigned.apk"
    output="$output_dir/oko-test-$abi.apk"
    "$tools/apksigner" sign --ks "$store" "${store_type[@]}" --ks-key-alias "$key_alias" \
        --ks-pass env:OKO_APK_STORE_PASSWORD --key-pass env:OKO_APK_KEY_PASSWORD \
        --out "$output" "$apk"
    verification=$("$tools/apksigner" verify --print-certs "$output")
    printf '%s\n%s\n' "$abi" "$verification"
    mapfile -t signers < <(
        sed -nE 's/^(Signer #[0-9]+|V[0-9.]+ Signer:?) certificate SHA-256 digest: ([a-f0-9]{64})$/\2/p' <<< "$verification" |
            sort -u
    )
    if [[ ${#signers[@]} -ne 1 || "${signers[0]:-}" != "$expected_sha" ]]; then
        echo "::error::Unexpected APK signing certificate for $abi."
        exit 1
    fi
done

printf 'artifact=%s\n' "$artifact" >> "$GITHUB_OUTPUT"
printf 'OKO test certificate SHA-256: `%s` (%s)\n' "$expected_sha" "$artifact" >> "$GITHUB_STEP_SUMMARY"
