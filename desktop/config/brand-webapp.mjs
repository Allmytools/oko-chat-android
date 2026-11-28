import { copyFile, cp, mkdir } from "node:fs/promises";
import { join } from "node:path";
import { linkWallpaper, wallpaperFiles } from "./wallpaper.mjs";

const [webapp, verifiedWallpapers] = process.argv.slice(2);
if (!webapp || !verifiedWallpapers) {
    throw new Error("Usage: node desktop/config/brand-webapp.mjs <webapp> <verified-wallpapers>");
}

const brand = new URL("../../branding/oko/desktop/", import.meta.url);
const destination = join(webapp, "themes", "oko");
await linkWallpaper(webapp);
await mkdir(join(destination, "wallpaper"), { recursive: true });

for (const folder of ["login", "home", "icons"]) {
    await cp(new URL(folder + "/", brand), join(destination, folder), { recursive: true });
}
await cp(new URL("./fonts/", import.meta.url), join(destination, "fonts"), { recursive: true });
await copyFile(new URL("wallpaper/oko-wallpaper.css", brand), join(destination, "oko-wallpaper.css"));
for (const file of wallpaperFiles) {
    await copyFile(join(verifiedWallpapers, file), join(destination, "wallpaper", file));
}
