import assert from "node:assert/strict";
import { mkdir, stat } from "node:fs/promises";
import { createRequire } from "node:module";
import { join, resolve } from "node:path";
import { setTimeout } from "node:timers/promises";

const [webDirectory, output] = process.argv.slice(2);
const require = createRequire(resolve(process.env.OKO_PLAYWRIGHT_DIR ?? ".", "package.json"));
const { chromium, expect } = require("@playwright/test");

const webOrigin = "http://127.0.0.1:8123";
const homeserver = "https://m.pro.ua";
const account = "@demo:m.pro.ua";
const otherMember = "@colleague:m.pro.ua";
const roomId = "!oko:m.pro.ua";
const roomName = "Тестова кімната OKO";
const sampleMessage = "Вітаю! Це тестовий чат OKO.";

function matrixEvent(sequence, type, sender, content, stateKey) {
    return {
        event_id: "$demo" + sequence,
        origin_server_ts: Date.UTC(2026, 10, 26, 9, 0, sequence),
        type,
        sender,
        content,
        ...(stateKey === undefined ? {} : { state_key: stateKey }),
    };
}

const members = [
    matrixEvent(3, "m.room.member", account, { membership: "join", displayname: "Демо OKO" }, account),
    matrixEvent(4, "m.room.member", otherMember, { membership: "join", displayname: "Колега" }, otherMember),
];
const state = {
    events: [
        matrixEvent(1, "m.room.create", account, { creator: account, room_version: "10" }, ""),
        matrixEvent(2, "m.room.name", account, { name: roomName }, ""),
        ...members,
    ],
};
const messages = [
    matrixEvent(5, "m.room.message", otherMember, { msgtype: "m.text", body: sampleMessage }),
    matrixEvent(6, "m.room.message", account, { msgtype: "m.text", body: "Перевіряю вигляд повідомлень." }),
    matrixEvent(7, "m.room.member", "@visitor:m.pro.ua", { membership: "join", displayname: "Відвідувач" }, "@visitor:m.pro.ua"),
];

async function serveMatrixFixture(route, url) {
    const headers = {
        "access-control-allow-origin": "*",
        "access-control-allow-headers": "authorization, content-type",
        "access-control-allow-methods": "GET, POST, PUT, OPTIONS",
    };
    const reply = (json) => route.fulfill({ headers, json });
    const notFound = () => route.fulfill({
        headers, status: 404, json: { errcode: "M_NOT_FOUND", error: "No fixture data" },
    });
    const method = route.request().method();
    const endpoint = url.pathname.split("/").filter(Boolean).at(-1);

    if (method === "OPTIONS") return route.fulfill({ headers, status: 204 });
    if (url.pathname === "/.well-known/matrix/client") return reply({ "m.homeserver": { base_url: homeserver } });
    if (url.pathname.includes("/account_data/")) return method === "GET" ? notFound() : reply({});
    if (url.pathname.includes("/receipt/")) return reply({});

    switch (endpoint) {
        case "auth_metadata":
            return route.fulfill({ headers, status: 404, json: { errcode: "M_UNRECOGNIZED", error: "No OIDC" } });
        case "versions": return reply({ versions: ["v1.11", "v1.12"], unstable_features: {} });
        case "login": return reply({ flows: [{ type: "m.login.password" }] });
        case "whoami": return reply({ user_id: account, device_id: "DEMO", is_guest: false });
        case "filter": return reply({ filter_id: "demo-filter" });
        case "capabilities": return reply({ capabilities: {} });
        case "turnServer": return reply({ uris: [], ttl: 3600 });
        case "protocols": return reply({});
        case "members": return reply({ chunk: members });
        case "messages": return reply({ start: "before-fixture", chunk: [] });
        case "pushrules": return reply({ global: { content: [], override: [], room: [], sender: [], underride: [] } });
        case "upload": return reply({ one_time_key_counts: { signed_curve25519: 50 } });
        case "query":
            await setTimeout(80);
            return reply({ device_keys: {}, failures: {} });
        case "sync": {
            const since = url.searchParams.has("since");
            if (since) await setTimeout(300);
            return reply({
                next_batch: "demo",
                rooms: {
                    join: {
                        [roomId]: {
                            state,
                            timeline: { prev_batch: "before-fixture", limited: false, events: since ? [] : messages },
                            summary: { "m.joined_member_count": 2, "m.invited_member_count": 0 },
                            unread_notifications: { highlight_count: 0, notification_count: 0 },
                            account_data: { events: [{ type: "m.fully_read", content: { event_id: "$demo5" } }] },
                            ephemeral: { events: [{ type: "m.receipt", content: { "$demo6": { "m.read": { [otherMember]: { ts: Date.UTC(2026, 10, 26, 9, 0, 7) } } } } }] },
                        },
                    },
                },
                device_one_time_keys_count: { signed_curve25519: 50 },
                device_unused_fallback_key_types: ["signed_curve25519"],
            });
        }
        default:
            return url.pathname.includes("/profile/") ? reply({ displayname: "Демо OKO" }) : notFound();
    }
}

async function serveFixture(route) {
    const url = new URL(route.request().url());
    if (url.origin === homeserver) return serveMatrixFixture(route, url);
    if (url.origin !== webOrigin) return route.abort();

    const file = join(webDirectory, url.pathname === "/" ? "index.html" : url.pathname.slice(1));
    if (!(await stat(file).catch(() => null))?.isFile()) return route.fulfill({ status: 404 });
    return route.fulfill({ path: file });
}

await mkdir(output, { recursive: true });
const browser = await chromium.launch(process.env.OKO_CHROMIUM
    ? { executablePath: process.env.OKO_CHROMIUM }
    : process.platform === "win32" ? { channel: "msedge" } : {});

try {
    const themes = [
        ["light", "custom-OKO світла", "#008069", "rgb(239, 234, 226)"],
        ["dark", "custom-OKO темна", "#21c063", "rgb(16, 19, 23)"],
        ["light", "light"],
        ["dark", "dark"],
        ["light", "light-hc"],
        ["dark", "dark-hc"],
    ];
    const viewports = [{ width: 1440, height: 900 }];
    if (process.argv.includes("--web")) viewports.push({ width: 480, height: 900 });
    const cases = viewports.flatMap((viewport) => themes.map((theme) => [viewport, ...theme]));
    for (const [viewport, colorScheme, theme, accent, background] of cases) {
        const filename = (screen) => join(output, screen + "-" + viewport.width + "x" + viewport.height + ".png");
        const context = await browser.newContext({
            locale: "en-US",
            colorScheme,
            viewport,
            serviceWorkers: "block",
        });
        await context.addInitScript(({ theme, webOrigin }) => {
            if (location.origin !== webOrigin) return;
            localStorage.setItem("mx_local_settings", JSON.stringify({ use_system_theme: false, theme }));
        }, { theme, webOrigin });
        await context.route("**/*", serveFixture);
        const page = await context.newPage();

        try {
            await page.goto(webOrigin + "/#/login");
            await expect(page.getByRole("heading", { name: "Sign in" })).toBeVisible({ timeout: 20000 });
            await expect(page.getByRole("button", { name: "Sign in", exact: true })).toBeEnabled();
            await expect(page.getByText("Your OKO is misconfigured", { exact: true })).toBeHidden();
            await expect(page.locator('img[src="themes/oko/login/logo.svg"]')).toBeVisible();
            await expect(page.getByRole("link", { name: "Create account" })).toHaveCount(0);
            const actualAccent = await page.locator("body").evaluate((body) =>
                getComputedStyle(body).getPropertyValue("--cpd-color-bg-accent-rest").trim().toLowerCase());
            if (background) {
                assert.equal(actualAccent, accent);
                const onest = await page.evaluate(async () => (await document.fonts.load('400 14px Onest', "OKO ОКО")).length);
                assert.ok(onest > 0, "Expected the packaged Onest font");
                await page.screenshot({ path: filename("login-" + colorScheme), animations: "disabled" });
            }

            // Use the upstream UI test hook with a synthetic user and intercepted Matrix requests.
            await page.evaluate((hs) => window.mxLoginWithAccessToken(hs, "demo-token"), homeserver);
            await page.goto(webOrigin + "/#/home");
            await expect(page.getByRole("heading", { name: "Вітаємо в OKO" })).toBeVisible({ timeout: 20000 });
            await expect(page.locator('img[src="themes/oko/home/oko_znak.svg"]')).toBeVisible();

            const room = page.getByText(roomName, { exact: true }).first();
            await expect(room).toBeVisible({ timeout: 10000 });

            // Dismiss the media notice and room-list tip in the synthetic browser profile.
            const dismiss = page.getByRole("alert").getByRole("button", { name: /^ok$/i }).first();
            await dismiss.click({ timeout: 1500 }).catch(() => {});
            await dismiss.click({ timeout: 1500 }).catch(() => {});
            const verificationNudge = page.getByRole("alert").filter({ hasText: "Verify this device" });
            await verificationNudge.getByRole("button", { name: "Later", exact: true }).click({ timeout: 1500 }).catch(() => {});
            if (background) {
                await page.screenshot({ path: filename("home-" + colorScheme), animations: "disabled" });
            }

            await page.evaluate((id) => { location.hash = "/room/" + id; }, roomId);
            await expect(page.getByText(sampleMessage, { exact: true })).toBeVisible({ timeout: 15000 });
            const timeline = page.locator(".mx_RoomView_timeline > .mx_RoomView_messagePanel");
            await expect(timeline).toHaveCount(1);
            await expect(timeline).toBeVisible();
            const imagesIn = (selector) =>
                page.locator(selector).evaluateAll((nodes) => nodes.map((node) => getComputedStyle(node).backgroundImage));
            for (const selector of [".mx_LeftPanel", ".mx_MessageComposer"]) {
                const images = await imagesIn(selector);
                if (viewport.width >= 768) assert.ok(images.length > 0, "Expected Element selector " + selector);
                assert.ok(images.every((image) => !image.includes("khaos_chystyi")), "Wallpaper leaked into " + selector);
            }
            if (background) await expect(timeline).toHaveCSS("background-color", background);
            if (theme === "custom-OKO світла") {
                const size = viewport.width < viewport.height ? "phone_1290x2796" : "3840x2160";
                const image = webOrigin + "/themes/oko/wallpaper/interatletika_khaos_chystyi_" + size + ".png";
                await expect(timeline).toHaveCSS("background-image", 'url("' + image + '")');
                await expect(timeline).toHaveCSS("background-repeat", "no-repeat");
                await expect(timeline).toHaveCSS("background-size", "cover");
                await expect(timeline).toHaveCSS("background-position", "50% 50%");
                await expect(timeline).toHaveCSS("background-attachment", "scroll");
                await timeline.evaluate(async (_element, url) => {
                    const wallpaper = new Image();
                    wallpaper.src = url;
                    await wallpaper.decode();
                }, image);
                const serviceRows = await timeline.locator(
                    ".mx_MessageTimestamp, .mx_EventTile_timestamp, .mx_TextualEvent, .mx_DateSeparator, " +
                    ".mx_EventTile_readMarker, .mx_RoomView_myReadMarker, .mx_ReadReceiptMarker",
                ).evaluateAll((elements) => elements.filter((element) => element.getClientRects().length).map((element) => {
                    const style = getComputedStyle(element);
                    return { classes: element.className, foreground: style.color, background: style.backgroundColor };
                }));
                console.log("Timeline service styles", viewport.width, JSON.stringify(serviceRows));
                await page.screenshot({ path: filename("chat-light"), animations: "disabled" });
            } else {
                await expect(timeline).toHaveCSS("background-image", "none");
                if (theme === "custom-OKO темна" || theme === "light") {
                    await page.screenshot({
                        path: filename(theme === "light" ? "chat-element-light" : "chat-dark"),
                        animations: "disabled",
                    });
                }
            }
        } finally {
            await context.close();
        }
    }
} finally {
    await browser.close();
}
