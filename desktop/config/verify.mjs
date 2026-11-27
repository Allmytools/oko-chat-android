import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import { join } from "node:path";
import { checkTimelineSelectors, wallpaperLink } from "./wallpaper.mjs";

const [webapp, expectedConfig, desktopResources, installerGif] = process.argv.slice(2);
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
assert.equal(config.setting_defaults["UIFeature.registration"], false);
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

await checkTimelineSelectors(webapp);
const index = await readFile(join(webapp, "index.html"), "utf8");
assert.equal(index.split(wallpaperLink).length - 1, 1, "Expected one wallpaper stylesheet link");

for (const name of ["light", "dark"]) {
    const theme = config.setting_defaults.custom_themes.find(({ is_dark }) => is_dark === (name === "dark"));
    const marker = "oko-wallpaper-" + name;
    assert.equal(theme.colors[marker], name === "dark" ? "#101317" : "#F4F0E9");
    assert.equal(Object.keys(theme.colors).filter((key) => key.startsWith("oko-wallpaper-")).length, 1);
}

for (const name of ["oko-wallpaper.css", "wallpaper_light.svg", "wallpaper_dark.svg"]) {
    const packaged = await readFile(join(webapp, "themes/oko", name));
    assert.deepEqual(packaged, await readFile(new URL("wallpaper/" + name, brand)), name);
}
for (const name of ["icon.ico", "icon.png"]) {
    const packaged = await readFile(join(desktopResources, "build", name));
    assert.deepEqual(packaged, await readFile(new URL("icons/" + name, brand)), name);
}
const gif = await readFile(installerGif);
assert.deepEqual(gif, await readFile(new URL("install-spinner.gif", brand)), "Squirrel installer GIF");
assert.equal(gif.toString("ascii", 0, 6), "GIF89a");
assert.equal(gif.readUInt16LE(6), 320);
assert.equal(gif.readUInt16LE(8), 320);
