# Реєстр змін OKO

## ЧА2 — OKO для Windows

| Файл і рядки | Зміна | Зона | Перевірка |
|---|---|---|---|
| `desktop/ELEMENT_WEB_TAG:1` | Єдине джерело версії Element Web і Desktop | 🟢 новий файл OKO | Тег збігається з випуском GitHub і містить `apps/desktop` |
| `desktop/variant.test.json:1–7`, `desktop/variant.json:1–7` | Окремі ідентифікатори, назви й схеми посилань OKO для тестової та робочої версій | 🟢 нові файли OKO | У збірці використано тестовий варіант |
| `desktop/config/generate.mjs:1–39` | Генерує налаштування з пакета дизайну, із заглушкою сервера й ручним вибором адреси | 🟢 новий файл OKO | Згенерувати JSON і звірити обидві теми, оформлення входу й домашньої сторінки |
| `docs/oko/PROTOKOL.md`, розділ ЧА2 | Статус етапів | 🟢 документація OKO | Звірити з історією гілки |
| `.github/workflows/oko-desktop-build.yml:1–142` | Перевіряє випуск Element і збирає тестовий інсталятор Windows та SHA-256 | 🟢 новий workflow OKO | Зелений `oko-desktop-build`, артефакт `oko-desktop-windows-test` |
| `desktop/config/verify.mjs:1–33` | Звіряє конфігурацію й ресурси у файлі `webapp.asar` збірки | 🟢 новий файл OKO | Крок перевірки пакета у workflow |
| `desktop/screenshots/capture.mjs:1–170` | Знімає вхід, домашню сторінку й чат у двох темах із вигаданою кімнатою Matrix | 🟢 новий сценарій OKO | Шість PNG 1920×1080 в артефакті `oko-desktop-screenshots` |
| `docs/oko/zvity/ZVIT_CHA2.md:1–26` | Встановлення, оновлення й варіанти системної теми | 🟢 документація OKO | Звірити з результатом workflow й вихідним кодом випуску |
