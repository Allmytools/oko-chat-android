import assert from "node:assert/strict";
import { mkdir, stat } from "node:fs/promises";
import { createRequire } from "node:module";
import { join, resolve } from "node:path";
import { setTimeout } from "node:timers/promises";

const [webDirectory, output] = process.argv.slice(2);
const require = createRequire(resolve("package.json"));
const { chromium, expect } = require("@playwright/test");

const webOrigin = "http://127.0.0.1:8123";
const homeserver = "https://matrix.example";
const account = "@demo:matrix.example";
const otherMember = "@colleague:matrix.example";
const roomId = "!oko:matrix.example";
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
    : { channel: "msedge" });

try {
    for (const [colorScheme, theme, accent] of [
        ["light", "custom-OKO світла", "#008069"],
        ["dark", "custom-OKO темна", "#21c063"],
    ]) {
        const context = await browser.newContext({
            locale: "en-US",
            colorScheme,
            viewport: { width: 1920, height: 1080 },
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
            await expect(page.locator('img[src="themes/oko/login/logo.svg"]')).toBeVisible();
            await expect(page.getByRole("link", { name: "Create account" })).toHaveCount(0);
            const actualAccent = await page.locator("body").evaluate((body) =>
                getComputedStyle(body).getPropertyValue("--cpd-color-bg-accent-rest").trim().toLowerCase());
            assert.equal(actualAccent, accent);
            await page.screenshot({ path: join(output, "login-" + colorScheme + ".png"), animations: "disabled" });

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
            await page.screenshot({ path: join(output, "home-" + colorScheme + ".png"), animations: "disabled" });

            await page.evaluate((id) => { location.hash = "/room/" + id; }, roomId);
            await expect(page.getByText(sampleMessage, { exact: true })).toBeVisible({ timeout: 15000 });
            await page.screenshot({ path: join(output, "chat-" + colorScheme + ".png"), animations: "disabled" });
        } finally {
            await context.close();
        }
    }
} finally {
    await browser.close();
}
