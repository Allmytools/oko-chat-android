import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import { join } from "node:path";

const [webapp, expectedConfig] = process.argv.slice(2);
const readJson = async (path) => JSON.parse(await readFile(path, "utf8"));
const config = await readJson(join(webapp, "config.json"));

assert.deepEqual(config, await readJson(expectedConfig));
assert.equal(config.brand, "OKO");
assert.equal(config.setting_defaults.layout, "bubble");
assert.equal(config.default_theme, "custom-OKO світла");
assert.deepEqual(
    config.setting_defaults.custom_themes.map(({ name, is_dark }) => [name, is_dark]),
    [["OKO світла", false], ["OKO темна", true]],
);
assert.equal(config.disable_guests, true);
assert.equal(config.disable_registration, true);
assert.equal(config.disable_custom_urls, false);
assert.equal(Object.hasOwn(config, "update_base_url"), false);

const brand = new URL("../../branding/oko/desktop/", import.meta.url);
const assets = [
    config.branding.welcome_background_url,
    config.branding.auth_header_logo_url,
    config.embedded_pages.home_url,
    "themes/oko/home/oko_znak.svg",
];
for (const asset of assets) {
    const packaged = await readFile(join(webapp, asset));
    const original = await readFile(new URL(asset.replace(/^themes\/oko\//, ""), brand));
    assert.deepEqual(packaged, original, asset);
}
