import { copyFile, cp, mkdir } from "node:fs/promises";
import { join } from "node:path";
import { linkWallpaper } from "./wallpaper.mjs";

const [webapp] = process.argv.slice(2);
const brand = new URL("../../branding/oko/desktop/", import.meta.url);
const destination = join(webapp, "themes", "oko");
await linkWallpaper(webapp);
await mkdir(destination, { recursive: true });

for (const folder of ["login", "home", "icons"]) {
    await cp(new URL(folder + "/", brand), join(destination, folder), { recursive: true });
}
for (const file of ["oko-wallpaper.css", "wallpaper_light.svg", "wallpaper_dark.svg"]) {
    await copyFile(new URL("wallpaper/" + file, brand), join(destination, file));
}
