#!/usr/bin/env bash
set -euo pipefail

root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
tag=$(tr -d '\r\n' < "$root/desktop/ELEMENT_WEB_TAG")
[[ $tag =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]
work=$1
wallpapers=$2
release="element-$tag.tar.gz"
url="https://github.com/element-hq/element-web/releases/download/$tag/$release"
fingerprint=712BFBEE92DCA45252DB17D7C7BE97EFA179B100

mkdir -p "$work/webapp" "$work/artifacts"
cd -- "$work"
curl --fail --silent --show-error --location --retry 3 --output "$release" "$url"
curl --fail --silent --show-error --location --retry 3 --output "$release.asc" "$url.asc"
curl --fail --silent --show-error --location --retry 3 --output element-release-key.asc \
    https://packages.element.io/element-release-key.asc
mkdir -m 700 gpg
gpg --batch --homedir "$work/gpg" --import element-release-key.asc
gpg --batch --homedir "$work/gpg" --status-fd 1 --verify "$release.asc" "$release" > signature-status
awk -v fingerprint="$fingerprint" '$2 == "VALIDSIG" && ($3 == fingerprint || $NF == fingerprint) { verified = 1 } END { exit !verified }' signature-status
sha256sum "$release"

tar -xzf "$release" --strip-components=1 -C webapp
node "$root/desktop/config/generate.mjs" "$work/config"
cp -- config/config.json webapp/config.json
node "$root/desktop/config/brand-webapp.mjs" "$work/webapp" "$wallpapers"
node "$root/desktop/config/verify.mjs" "$work/webapp" "$work/config/config.json" "$wallpapers"
tar -czf "artifacts/oko-web-$tag.tar.gz" -C webapp .
(cd artifacts && sha256sum "oko-web-$tag.tar.gz" > SHA256SUMS)
wc -c "artifacts/oko-web-$tag.tar.gz"
