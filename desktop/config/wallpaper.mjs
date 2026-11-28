import assert from "node:assert/strict";
import { readdir, readFile, writeFile } from "node:fs/promises";
import { join } from "node:path";

export const wallpaperLink = '<link rel="stylesheet" href="themes/oko/oko-wallpaper.css">';
export const wallpaperFiles = [
    "interatletika_khaos_chystyi_3840x2160.png",
    "interatletika_khaos_chystyi_phone_1290x2796.png",
];
const selectors = [".mx_RoomView_timeline", ".mx_RoomView_messagePanel"];

export async function checkTimelineSelectors(webapp) {
    const bundles = await readdir(join(webapp, "bundles"), {
        recursive: true,
        withFileTypes: true,
    });
    for (const theme of ["light", "dark"]) {
        const filename = "theme-" + theme + "-custom.css";
        const matches = bundles.filter((entry) => entry.isFile() && entry.name === filename);
        assert.equal(matches.length, 1, "Expected one Element " + filename);
        const [bundle] = matches;
        const css = await readFile(join(bundle.parentPath, bundle.name), "utf8");
        for (const selector of selectors) {
            const pattern = new RegExp("\\." + selector.slice(1) + "(?![\\w-])");
            assert.match(css, pattern, "Шпалери OKO: селектор " + selector + " зник з " + filename);
        }
    }
}

export async function linkWallpaper(webapp) {
    await checkTimelineSelectors(webapp);
    const index = join(webapp, "index.html");
    const html = await readFile(index, "utf8");
    assert.equal(html.match(/<\/head>/gi)?.length, 1, "Expected one closing head in Element index.html");
    assert.ok(!html.includes(wallpaperLink), "Wallpaper stylesheet is already linked");
    await writeFile(index, html.replace(/<\/head>/i, "  " + wallpaperLink + "\n  </head>"));
}
