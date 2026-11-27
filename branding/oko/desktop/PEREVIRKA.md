# ДД1 — OKO для комп'ютера (Element Desktop / Web): перевірка пакета (дизайнер «ОКО · Дизайн чату», 26.11.2026)

## 1. Склад
| Файл | Що | Куди (для ЧА2) |
|---|---|---|
| `theme_oko_light.json`, `theme_oko_dark.json` | теми у форматі `setting_defaults.custom_themes` (блоки `colors` і `compound`) | вставити обидва об'єкти в масив `custom_themes` у `config.json` |
| `config_branding.json` | `brand`, `branding`, `embedded_pages`, `default_theme` | злити з `config.json`; шляхи `themes/oko/…` — відносно кореня Element Web (тека кладеться в `webapp/themes/oko/`) |
| `login/background.jpg` | фон входу 1920×1080, світлий, без тексту | `welcome_background_url` |
| `login/logo.svg` | логотип 200×60 (знак B4 + «OKO» контурами, зелений — читається на світлій і темній картці) | `auth_header_logo_url` |
| `home/home.html`, `home/oko_znak.svg` | домашня сторінка | `embedded_pages.home_url` |
| `icons/icon.png` | 512×512, прозорий фон | значок програми (Linux/інсталятор) |
| `icons/icon.ico` | 16, 24, 32 (спрощений знак) + 48, 64, 128, 256 (повний B4) | Windows: панель завдань, трей, інсталятор |
| `icons/favicon.ico` | 16, 32 (спрощений) + 48 (повний) | Element Web, вкладка браузера |
| `icons/icon-192.png`, `icons/icon-512.png` | PNG для вебу | маніфест Element Web |
| `icons/icon.svg`, `icons/icon_mini.svg` | вихідні вектори повного й спрощеного знака | — |
| `install-spinner.gif` | анімація інсталятора Windows 320×320, 48 кадрів × 40 мс: значок B4, крапки в бульбашці «друкують» (власник обрав 27.11, варіант C) | `electron-builder` → Squirrel `loadingGif` (замість анімації Element) |
| `wallpaper/` | **ПРОПОЗИЦІЯ, не погоджено:** шпалери чату «ОКО» (власні контурні значки: око, бульбашка, документ, лупа, гойдалка, гантель…), безшовна плитка 480×480, світла (фон `#F2F7F5`) і темна (фон `#101317`) | лише після погодження інтегратора — див. розділ 5, п. 6 |
| `previews/` | вхід, кімната + чат, домашня — світла й темна, 1920×1080 | для звірки; затвердження — за знімками збірки |

Не зроблено (не потрібно або чекає рішення): `fonts/` — власник вирішив шрифт не додавати (Element Web і так на Inter);
 значок Mac — не робимо.

## 2. Зони
| Позиція | Зона | Статус |
|---|---|---|
| Теми OKO (акцент, бейджі, рамки, значки, градієнт) | 🟢 | зроблено |
| Основні кнопки `bg-action-primary-*` кольором бренду | 🟡 | **погоджено інтегратором 26.11** і для комп'ютера (контраст 4,9 / 7,8 : 1) |
| Фон, основний і другорядний текст, критичний (червоний) колір | — | **як у Element** (Compound 11.3.1), не змінено |
| `username-colors`, `avatar-background-colors` | 🟢 | 1–6 — значення Element (decorative 1–6); 7–8 додано дизайнером (бірюзовий, оливковий) з контрастом ≥ 4,5 : 1 |
| Назва `brand: "OKO"` | 🟢 | |
| Сторінка входу: фон, логотип | 🟢 | |
| Посилання внизу входу (`auth_footer_links`) | 🟢 | **без посилань** — рішення власника 26.11 |
| Домашня сторінка | 🟢 | **текст правил — чернетка**; посилання «Відкрити програму ОКО» **вимкнено** (закоментовано в `home.html`) — увімкнути, коли в ОКО з'явиться HTTPS-адреса (рішення власника 26.11) |
| Свій шрифт | 🟢 | власник: **не додаємо** |
| Будова екранів, кнопки, меню, розміри, тексти інтерфейсу, символіка Element, шпалери | 🔴 | не входить |

## 3. Кольори = Android (`brand.json`)
Перетворення назви: `bgAccentRest` → `--cpd-color-bg-accent-rest`; `gradientActionStop1` → `--cpd-color-gradient-action-stop1`.
| Android | Element Web | світла Android | світла Web | темна Android | темна Web | збіг |
|---|---|---|---|---|---|---|
| `bgAccentRest` | `--cpd-color-bg-accent-rest` | #008069 | #008069 | #21C063 | #21C063 | ✅ |
| `bgAccentHovered` | `--cpd-color-bg-accent-hovered` | #00715D | #00715D | #3CCB78 | #3CCB78 | ✅ |
| `bgAccentPressed` | `--cpd-color-bg-accent-pressed` | #006352 | #006352 | #5BD48D | #5BD48D | ✅ |
| `bgAccentSubtle` | `--cpd-color-bg-accent-subtle` | #EEF8F5 | #EEF8F5 | #04200F | #04200F | ✅ |
| `bgBadgeAccent` | `--cpd-color-bg-badge-accent` | #C9EDE4 | #C9EDE4 | #0F3A22 | #0F3A22 | ✅ |
| `textBadgeAccent` | `--cpd-color-text-badge-accent` | #005C4B | #005C4B | #5BD48D | #5BD48D | ✅ |
| `borderAccentPrimary` | `--cpd-color-border-accent-primary` | #008069 | #008069 | #21C063 | #21C063 | ✅ |
| `borderAccentSubtle` | `--cpd-color-border-accent-subtle` | #2FB394 | #2FB394 | #146B3A | #146B3A | ✅ |
| `gradientActionStop1` | `--cpd-color-gradient-action-stop1` | #8FD6C4 | #8FD6C4 | #5BD48D | #5BD48D | ✅ |
| `gradientActionStop2` | `--cpd-color-gradient-action-stop2` | #2E9E86 | #2E9E86 | #21C063 | #21C063 | ✅ |
| `gradientActionStop3` | `--cpd-color-gradient-action-stop3` | #008069 | #008069 | #168A46 | #168A46 | ✅ |
| `gradientActionStop4` | `--cpd-color-gradient-action-stop4` | #005C4B | #005C4B | #0B4A26 | #0B4A26 | ✅ |
| `iconAccentPrimary` | `--cpd-color-icon-accent-primary` | #008069 | #008069 | #21C063 | #21C063 | ✅ |
| `iconAccentTertiary` | `--cpd-color-icon-accent-tertiary` | #009B7E | #009B7E | #1A9A4F | #1A9A4F | ✅ |
| `textActionAccent` | `--cpd-color-text-action-accent` | #008069 | #008069 | #21C063 | #21C063 | ✅ |
| 🟡 `bgActionPrimaryRest` | `--cpd-color-bg-action-primary-rest` | #008069 | #008069 | #21C063 | #21C063 | ✅ |
| 🟡 `bgActionPrimaryHovered` | `--cpd-color-bg-action-primary-hovered` | #00715D | #00715D | #3CCB78 | #3CCB78 | ✅ |
| 🟡 `bgActionPrimaryPressed` | `--cpd-color-bg-action-primary-pressed` | #006352 | #006352 | #5BD48D | #5BD48D | ✅ |

Застарілий блок `colors` теми: `accent-color` = `primary-color` = акцент (#008069 / #21C063); решта — значення Element.

## 4. Контраст (WCAG 2.x; фон: світла #FFFFFF, темна #101317; текст на суцільному: білий / #101317)
| Тема | Що | Кольори | Контраст | Норма | |
|---|---|---|---|---|---|
| світла | text-action-accent на фоні | `#008069` на `#FFFFFF` | 4.89 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | text-on-solid-primary на bg-accent-rest | `#FFFFFF` на `#008069` | 4.89 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | 🟡 text-on-solid-primary на bg-action-primary-rest | `#FFFFFF` на `#008069` | 4.89 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | text-on-solid-primary на bg-accent-hovered | `#FFFFFF` на `#00715D` | 5.97 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | 🟡 text-on-solid-primary на bg-action-primary-hovered | `#FFFFFF` на `#00715D` | 5.97 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | text-on-solid-primary на bg-accent-pressed | `#FFFFFF` на `#006352` | 7.22 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | 🟡 text-on-solid-primary на bg-action-primary-pressed | `#FFFFFF` на `#006352` | 7.22 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | icon-accent-primary на фоні | `#008069` на `#FFFFFF` | 4.89 : 1 | ≥ 3 : 1 | ✅ |
| світла | border-accent-primary на фоні | `#008069` на `#FFFFFF` | 4.89 : 1 | ≥ 3 : 1 | ✅ |
| світла | text-badge-accent на bg-badge-accent | `#005C4B` на `#C9EDE4` | 6.35 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | лічильник непрочитаних: текст на accent-color | `#FFFFFF` на `#008069` | 4.89 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | username-colors[1] на фоні | `#005F00` на `#FFFFFF` | 7.96 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | username-colors[2] на фоні | `#00548C` на `#FFFFFF` | 7.93 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | username-colors[3] на фоні | `#822198` на `#FFFFFF` | 8.01 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | username-colors[4] на фоні | `#5D26CD` на `#FFFFFF` | 8.00 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | username-colors[5] на фоні | `#9F0850` на `#FFFFFF` | 8.00 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | username-colors[6] на фоні | `#9B2200` на `#FFFFFF` | 8.01 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | username-colors[7] на фоні | `#0B5F5A` на `#FFFFFF` | 7.50 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | username-colors[8] на фоні | `#6B4F00` на `#FFFFFF` | 7.65 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | літера аватара [1] на avatar-background-colors[1] | `#005F00` на `#E0F8D9` | 7.05 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | літера аватара [2] на avatar-background-colors[2] | `#00548C` на `#E3F5F8` | 7.06 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | літера аватара [3] на avatar-background-colors[3] | `#822198` на `#FAEEFB` | 7.13 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | літера аватара [4] на avatar-background-colors[4] | `#5D26CD` на `#F1EFFF` | 7.06 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | літера аватара [5] на avatar-background-colors[5] | `#9F0850` на `#FFECF0` | 7.05 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | літера аватара [6] на avatar-background-colors[6] | `#9B2200` на `#FFEFE4` | 7.14 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | літера аватара [7] на avatar-background-colors[7] | `#0B5F5A` на `#DDF3F1` | 6.49 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | літера аватара [8] на avatar-background-colors[8] | `#6B4F00` на `#FBF1D6` | 6.80 : 1 | ≥ 4.5 : 1 | ✅ |
| світла | логотип входу (зелений «OKO», великий текст) на картці | `#008069` на `#FFFFFF` | 4.89 : 1 | ≥ 3 : 1 | ✅ |
| темна | text-action-accent на фоні | `#21C063` на `#101317` | 7.79 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | text-on-solid-primary на bg-accent-rest | `#101317` на `#21C063` | 7.79 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | 🟡 text-on-solid-primary на bg-action-primary-rest | `#101317` на `#21C063` | 7.79 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | text-on-solid-primary на bg-accent-hovered | `#101317` на `#3CCB78` | 8.87 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | 🟡 text-on-solid-primary на bg-action-primary-hovered | `#101317` на `#3CCB78` | 8.87 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | text-on-solid-primary на bg-accent-pressed | `#101317` на `#5BD48D` | 9.97 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | 🟡 text-on-solid-primary на bg-action-primary-pressed | `#101317` на `#5BD48D` | 9.97 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | icon-accent-primary на фоні | `#21C063` на `#101317` | 7.79 : 1 | ≥ 3 : 1 | ✅ |
| темна | border-accent-primary на фоні | `#21C063` на `#101317` | 7.79 : 1 | ≥ 3 : 1 | ✅ |
| темна | text-badge-accent на bg-badge-accent | `#5BD48D` на `#0F3A22` | 6.82 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | лічильник непрочитаних: текст на accent-color | `#101317` на `#21C063` | 7.79 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | username-colors[1] на фоні | `#56C02C` на `#101317` | 7.96 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | username-colors[2] на фоні | `#21BACD` на `#101317` | 7.96 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | username-colors[3] на фоні | `#D991DE` на `#101317` | 8.03 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | username-colors[4] на фоні | `#AD9CFE` на `#101317` | 7.95 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | username-colors[5] на фоні | `#FE84A2` на `#101317` | 8.01 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | username-colors[6] на фоні | `#F6913D` на `#101317` | 8.01 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | username-colors[7] на фоні | `#3FC4B4` на `#101317` | 8.66 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | username-colors[8] на фоні | `#D6B11E` на `#101317` | 9.02 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | літера аватара [1] на avatar-background-colors[1] | `#56C02C` на `#002600` | 7.02 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | літера аватара [2] на avatar-background-colors[2] | `#21BACD` на `#001B4E` | 7.08 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | літера аватара [3] на avatar-background-colors[3] | `#D991DE` на `#37004E` | 7.12 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | літера аватара [4] на avatar-background-colors[4] | `#AD9CFE` на `#22006A` | 7.02 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | літера аватара [5] на avatar-background-colors[5] | `#FE84A2` на `#450018` | 7.14 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | літера аватара [6] на avatar-background-colors[6] | `#F6913D` на `#470000` | 7.13 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | літера аватара [7] на avatar-background-colors[7] | `#3FC4B4` на `#00302B` | 6.70 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | літера аватара [8] на avatar-background-colors[8] | `#D6B11E` на `#3A2C00` | 6.60 : 1 | ≥ 4.5 : 1 | ✅ |
| темна | логотип входу (зелений «OKO», великий текст) на картці | `#008069` на `#1B1D22` | 3.45 : 1 | ≥ 3 : 1 | ✅ |

Стани Rest → Hovered → Pressed розрізняються на око (див. значення в розділі 3).
Значки `icon.ico`: 16–32 px — спрощений знак (кільце з відблиском без бульбашки), бо бульбашка на 16 px зливається;
перевірка в треї на світлій і темній панелі Windows — на справжній збірці (ЧА2).

## 5. Що перевірити на справжній збірці / питання
1. **Домашня сторінка.** Element очищує HTML домашньої сторінки: `<style>`, `<script>`, `<svg>` видаляються, `<img>` і
   атрибути `style` лишаються (код `EmbeddedPage.tsx`). Тому сторінка — лише інлайн-стилі з токенами Compound (сама
   підлаштовується під світлу/темну тему), знак — `<img src="themes/oko/home/oko_znak.svg">`. Шлях рахується від кореня
   Element Web — перевірити, що картинка відкривається.
2. **Бульбашки на комп'ютері — типово** (`setting_defaults.layout = "bubble"`, погоджено інтегратором 26.11, зелена зона;
   працівник може змінити). Кольори бульбашок бере Element з теми — перевірити на знімках збірки ЧА2.
3. Вітальна сторінка до входу: тексти Element не змінюємо; власну вітальну сторінку (`embedded_pages.welcome_url`) не
   робили — завдання її не містить.
4. **Тема «Як у системі» (знахідка інтегратора 26.11).** Element за замовчуванням вмикає «Як у системі» і тоді показує
   вбудовані теми Element, не OKO; через `config.json` не вимикається. Рішення — у ЧА2: спосіб без коду або інструкція
   працівникам (Налаштування → Вигляд → вимкнути «Як у системі» → OKO).
5. **Android (ЧА1): темна «моя» бульбашка — вирішено.** Власник 26.11 за порадою інтегратора обрав `#173A2C` (час `#808994` —
   3,53 : 1, як у самого Element 3,48). Внесено в `brand.json` Android (`prozorro-tracker`, гілка `ai/oko-chat-dyzain`, коміт `c0a4cc2`).
6. **Шпалери чату (пропозиція 27.11, 🟡 потребує погодження інтегратора).** Власник хоче фон із візерунком, як у WhatsApp.
   Готово: `wallpaper/` (SVG + PNG, безшовні), макети `previews/shpalery_svitla.png`, `previews/shpalery_temna.png`.
   Що змінюється: (а) у світлій темі фон стрічки `timeline-background-color` `#FFFFFF` → `#F2F7F5` (відхід від правила «фон як
   у Element» — потрібне рішення); (б) сам візерунок — окремий CSS лише для області повідомлень (не тема Element, а правка
   збірки) — спосіб і ризик при оновленнях оцінює інтегратор. Візерунок ледь помітний (лінії `#D5E4DD` на `#F2F7F5`,
   `#1D2427` на `#101317`), текст стоїть на бульбашках, тому контраст тексту не страждає. Кольори бульбашок на макеті орієнтовні.
