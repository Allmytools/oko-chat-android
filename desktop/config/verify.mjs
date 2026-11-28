import assert from "node:assert/strict";
import { createHash } from "node:crypto";
import { readFile, readdir } from "node:fs/promises";
import { basename, join } from "node:path";
import { checkTimelineSelectors, wallpaperFiles, wallpaperLink } from "./wallpaper.mjs";

const [webapp, expectedConfig, wallpapers, desktopResources, installerGif] = process.argv.slice(2);
const readJson = async (path) => JSON.parse(await readFile(path, "utf8"));
const config = await readJson(join(webapp, "config.json"));
const brand = new URL("../../branding/oko/desktop/", import.meta.url);

assert.deepEqual(config, await readJson(expectedConfig));
assert.equal(config.brand, "OKO");
assert.equal(config.setting_defaults.layout, "bubble");
assert.equal(config.default_theme, "custom-OKO світла");
assert.deepEqual(config.default_server_config, {
    "m.homeserver": { base_url: "https://m.pro.ua", server_name: "m.pro.ua" },
});
assert.deepEqual(
    config.setting_defaults.custom_themes.map(({ name, is_dark }) => [name, is_dark]),
    [["OKO світла", false], ["OKO темна", true]],
);
assert.equal(config.disable_guests, true);
assert.equal(config.disable_custom_urls, true);
for (const feature of ["registration", "passwordReset", "voip", "widgets"]) {
    assert.equal(config.setting_defaults["UIFeature." + feature], false);
}
assert.equal(Object.hasOwn(config, "update_base_url"), false);

const assets = [
    config.branding.welcome_background_url,
    config.branding.auth_header_logo_url,
    config.embedded_pages.home_url,
    "themes/oko/home/oko_znak.svg",
    "themes/oko/icons/icon.ico",
    "themes/oko/icons/icon.png",
    "themes/oko/oko-wallpaper.css",
];
for (const asset of assets) {
    const original = new URL(asset.endsWith(".css") ? "wallpaper/oko-wallpaper.css" : asset.slice("themes/oko/".length), brand);
    assert.deepEqual(await readFile(join(webapp, asset)), await readFile(original), asset);
}

await checkTimelineSelectors(webapp);
const index = await readFile(join(webapp, "index.html"), "utf8");
assert.equal(index.split(wallpaperLink).length - 1, 1, "Expected one wallpaper stylesheet link");
const manifest = await readFile(new URL("../../branding/oko/wallpaper_khaos/SHA256SUMS", import.meta.url), "utf8");
const wallpaperDirectory = join(webapp, "themes/oko/wallpaper");
assert.deepEqual((await readdir(wallpaperDirectory)).sort(), [...wallpaperFiles].sort());
for (const file of wallpaperFiles) {
    const expected = manifest.split(/\r?\n/).find((line) => line.endsWith("/" + file))?.split(" ")[0];
    const contents = await readFile(join(wallpaperDirectory, file));
    assert.deepEqual(contents, await readFile(join(wallpapers, file)), file);
    assert.equal(createHash("sha256").update(contents).digest("hex"), expected, file);
    console.log(file, "SHA-256", expected);
}
for (const theme of config.setting_defaults.custom_themes) {
    const name = theme.is_dark ? "dark" : "light";
    assert.equal(theme.colors["oko-wallpaper-" + name], theme.is_dark ? "#101317" : "#EFEAE2");
    assert.ok(theme.fonts.general.startsWith("Onest,"));
    for (const { src } of theme.fonts.faces) {
        for (const { url } of src) {
            assert.ok(url.startsWith("themes/oko/fonts/"));
            const expected = await readFile(new URL("./fonts/" + basename(url), import.meta.url));
            assert.deepEqual(await readFile(join(webapp, url)), expected, url);
        }
    }
}
if (desktopResources && installerGif) {
    for (const name of ["icon.ico", "icon.png"]) {
        const packaged = await readFile(join(desktopResources, "build", name));
        assert.deepEqual(packaged, await readFile(new URL("icons/" + name, brand)), name);
    }
    const gif = await readFile(installerGif);
    assert.deepEqual(gif, await readFile(new URL("install-spinner.gif", brand)), "Squirrel installer GIF");
    assert.equal(gif.toString("ascii", 0, 6), "GIF89a");
    assert.equal(gif.readUInt16LE(6), 320);
    assert.equal(gif.readUInt16LE(8), 320);
    console.log("Squirrel installer GIF SHA-256", createHash("sha256").update(gif).digest("hex"));
}
