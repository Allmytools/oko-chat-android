import { mkdir, readFile, writeFile } from "node:fs/promises";
import { join } from "node:path";

const output = process.argv[2];
if (!output) {
    throw new Error("Usage: node desktop/config/generate.mjs <output-directory>");
}

const brandDirectory = new URL("../../branding/oko/desktop/", import.meta.url);
const readBrandJson = async (name) =>
    JSON.parse(await readFile(new URL(name, brandDirectory), "utf8"));

const [{ brand, branding, embedded_pages, default_theme, setting_defaults }, light, dark] =
    await Promise.all([
        readBrandJson("config_branding.json"),
        readBrandJson("theme_oko_light.json"),
        readBrandJson("theme_oko_dark.json"),
    ]);

const config = {
    brand,
    branding,
    embedded_pages,
    default_theme,
    setting_defaults: { ...setting_defaults, custom_themes: [light, dark] },
    disable_custom_urls: false,
    disable_guests: true,
    disable_registration: true,
    // Placeholder. Enter the intended Matrix homeserver on the sign-in screen.
    default_server_config: {
        "m.homeserver": { base_url: "https://matrix.example" },
    },
};

await mkdir(output, { recursive: true });
await writeFile(join(output, "config.json"), JSON.stringify(config, null, 2) + "\n");
