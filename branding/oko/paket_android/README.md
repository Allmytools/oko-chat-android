# OKO (Android) — пакет бренду і завдання на вбудовування

**Від:** дизайн-агент «ОКО · Дизайн чату», 25.11.2026.
**Кому:** виконавець «ОКО · Чат Android» (завдання **ЧА1** — копія в цій теці: `ZAVDANNIA_CHA1_kopiia_2026-11-25.md`;
оригінал — документ проєкту ОКО `claude/OKO_zavdannia_chat_android_zbirka_2026-11-25.md`).
**Де лежить:** репозиторій `Allmytools/prozorro-tracker`, гілка `ai/oko-chat-dyzain`, тека
`docs/tz_interface/oko_chat_android/`.

Цей документ **доповнює ЧА1**, а не замінює його: етапи, репозиторій форка, підпис, збирання, звіт — за ЧА1. Тут — що
саме з пакета куди класти і які значення брати. Коли ЧА1 і цей документ розходяться — пиши в протокол, не вгадуй.

## 0. Прочитай першим
1. `PRAVYLA_dyzainu_2026-11-25.md` — правила (копія документа проєкту; розділ 9 — як пакет лягає в код).
2. `brand.json` — усі значення (назва, кольори, статус жовтих позицій).
3. `PEREVIRKA.md` — зони, таблиця контрасту, що перевірити на справжній збірці.

## 1. Склад пакета і куди що йде

| Файл пакета | Куди в форку | Примітка |
|---|---|---|
| `brand.json` → `app_name` = `OKO` | `BuildTimeConfig.APPLICATION_NAME`, `ApplicationConfig.PRODUCTION_APPLICATION_NAME` | латиницею. `DESKTOP_APPLICATION_NAME` не чіпати (ЧА1) |
| `icon/foreground.svg` | модуль `appicon/oko`: шар `foreground` адаптивного значка | 108×108 dp, вектор (кола з обвідкою + контури, без масок) → VectorDrawable |
| `icon/background.svg` | шар `background` | суцільний `#008069` — можна задати кольором |
| `icon/monochrome.svg` | шар `monochrome` | **містить SVG-маски** (вирізи крапок і проміжок навколо бульбашки): VectorDrawable масок не має → або перетвори в контури (path ops, `evenOdd`), або візьми растр `icon/monochrome_{108,162,216,324,432}.png` (mdpi…xxxhdpi). Перевір тематичний значок на Android 13+ |
| `icon/foreground_{108…432}.png` | запасний растр шару `foreground` | якщо вектор не перетворюється чисто |
| `icon/legacy_ic_launcher_{48,72,96,144,192}.png` | `mipmap-{m,h,xh,xxh,xxxh}dpi/ic_launcher.png` | круглий знак (власник обрав коло) |
| `icon/legacy_ic_launcher_round_{…}.png` | `mipmap-*/ic_launcher_round.png` | те саме |
| `icon/store_512.png` | метадані F-Droid / сторінка випуску | 512×512 |
| `notification/ic_notification.svg` | ресурс `drawable/ic_notification` у теці бренду модуля `app` | 24×24 dp, білий силует на прозорому; **без бульбашки** (на 16 px злипається) |
| `onboarding/onboarding_logo_light.png` | `drawable-xxhdpi/onboarding_logo.png` у теці бренду | 600×600, прозорий фон, знак + «OKO» |
| `onboarding/onboarding_logo_dark.png` | `drawable-night-xxhdpi/onboarding_logo.png` | те саме для темної теми |
| `splash/oko_splash_light.svg` | `drawable/oko_splash` (VectorDrawable) | 🟡 погоджено 25.11: `values/themes.xml` → `windowSplashScreenAnimatedIcon = @drawable/oko_splash` (замість `@drawable/transparent`) |
| `splash/oko_splash_dark.svg` | `drawable-night/oko_splash` | те саме в `values-night/themes.xml` |
| `splash/splash_icon.svg` | **не вбудовувати** | ранній ескіз, замінено на `oko_splash_*` |
| `previews/` | — | орієнтовні макети для звірки; затвердження — лише за знімками збірки |
| `icon/varianty/` | — | відхилені варіанти значка, лише для історії |

## 2. Палітра (зелена зона)
`brand.json` → `light` і `dark`: **15 токенів у кожній темі** (`bgAccent*`, `bgBadgeAccent`, `textBadgeAccent`,
`borderAccent*`, `gradientActionStop1…4`, `iconAccent*`, `textActionAccent`). Усі інші токени — значення Element без змін
(зокрема `bgAccentSelected`, `*Success*`, `textLinkExternal`, `borderFocused`, фон і текст).
- Світла: акцент `#008069`. Темна: акцент `#21C063`. Чорна (AMOLED) — з темної автоматично.
- `NotificationConfig.NOTIFICATION_ACCENT_COLOR` = `brand.json → notification_accent` (`#008069`).
- Якщо токена з такою назвою в поточному Element немає — **не вигадуй заміну**: запиши в протокол назву і зупини цей пункт.

## 3. Жовта зона — статус
| Позиція | Статус | Значення |
|---|---|---|
| Основні кнопки `bgActionPrimaryRest/Hovered/Pressed` | ✅ погоджено (ЧА1, шар 2) | `brand.json → yellow_zone.bgActionPrimary`. `…Disabled` — без змін |
| Бульбашки (`ColorAliases.kt`) | ✅ погоджено (ЧА1, шар 2) | світла: лише `messageFromMeBackground = #D9FDD3`; **`messageFromOtherBackground` у світлій не змінювати** (біле на білому фоні зникне). Темна: `messageFromMeBackground = #173A2C` (було `#144D37`, змінено 26.11), `messageFromOtherBackground = #21282C` |
| Значок на заставці (`themes.xml`) | ✅ погоджено інтегратором 25.11 | по одному рядку у `values/themes.xml` і `values-night/themes.xml`; файли `oko_splash` (див. 1); вимоги й перевірка — `PEREVIRKA.md` |
| «OKO» у верхній панелі (`HomeTopBar.kt`) | ⛔ ні (рішення інтегратора 25.11) | лишається «Бесіди» |

## 4. Що перевірити і показати власнику (знімки бренду, ЧА1 Е3)
Світла й темна: привітання (логотип по центру, вітального тексту Element немає), список бесід, чат зі своїми й чужими
бульбашками, поле вводу, налаштування, сповіщення в шторці, значок на робочому столі — звичайний і тематичний.
Окремо в звіті:
1. контраст другорядного тексту (час) на темній бульбашці `#173A2C` — очікувано 3,53 : 1, прийнято власником 26.11 (як у
   самого Element 3,48); перерахуй і вкажи фактичне значення;
2. контраст посилань на обох «моїх» бульбашках;
3. чи логотип привітання не обрізається і не виходить за поля 16 dp на вузькому екрані (360 dp).

## 5. Межі (нагадування)
Не змінюй файли Element поза переліком розділу 2 правил. Не малюй і не змінюй картинки — це робота дизайнера: потрібна
правка зображення → запис у протокол. Назва й логотип Element у бренді заборонені. Секретів у файлах немає і не має бути.
