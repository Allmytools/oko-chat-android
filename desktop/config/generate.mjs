import { mkdir, readFile, writeFile } from "node:fs/promises";
import { join } from "node:path";

const [output] = process.argv.slice(2);
if (!output) {
    throw new Error("Usage: node desktop/config/generate.mjs <output-directory>");
}

const readJson = async (path) => JSON.parse(await readFile(new URL(path, import.meta.url), "utf8"));
const [brand, theme, { setting_defaults: panelDefaults, ...policy }] = await Promise.all([
    readJson("../../branding/oko/desktop/config_branding.json"),
    readJson("./oko_element_themes.json"),
    readJson("./oko-panel-settings.json"),
]);

const config = {
    ...policy,
    brand: brand.brand,
    branding: brand.branding,
    embedded_pages: brand.embedded_pages,
    default_theme: theme.default_theme,
    setting_defaults: {
        ...panelDefaults,
        ...theme.setting_defaults,
        custom_themes: theme.setting_defaults.custom_themes.map((entry) => ({
            ...entry,
            colors: {
                ...entry.colors,
                ...(entry.is_dark ? { "oko-wallpaper-dark": "#101317" } : { "oko-wallpaper-light": "#EFEAE2" }),
            },
        })),
    },
    default_server_config: {
        "m.homeserver": { base_url: "https://m.pro.ua", server_name: "m.pro.ua" },
    },
};

await mkdir(output, { recursive: true });
await writeFile(join(output, "config.json"), JSON.stringify(config, null, 2) + "\n");
