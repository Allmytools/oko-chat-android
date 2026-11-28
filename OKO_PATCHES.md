# Реєстр змін OKO

Історію пораховано в `upstream/develop` від 26.11.2025 за Києвом, до `277805403f92`.

| Шар | Файл або ресурс | Рядки / зміна | Зона та зміни Element за рік | Перевірка |
|---|---|---|---|---|
| 1 | `plugins/src/main/kotlin/io.element.android-compose-application.gradle.kts` | Рядки 18 і 37: імпорт і виклик `configureOkoApplication` | Зелена, 3 | Перевірити `app_name` і значки в APK |
| 1 | `plugins/src/main/kotlin/oko/OkoApplication.kt` | Назви, джерела ресурсів FOSS, applicationId, схеми OAuth, суфікс .test і зовнішній підпис release APK | Новий файл, 0 | Перевірити назву кожного варіанта та фінальне злиття ресурсів |
| 1 | `appconfig/…/ApplicationConfig.kt` | Рядок 27: `PRODUCTION_APPLICATION_NAME` | Зелена, 0 | Мобільні назви OKO та окрема назва Element для Desktop |
| 1 | `appconfig/…/NotificationConfig.kt` | Рядок 31: `NOTIFICATION_ACCENT_COLOR` | Зелена, 0 | Акцент шторки збігається зі світлою палітрою |
| 1 | `appconfig/…/OnBoardingConfig.kt` | Рядок 13: `CAN_CREATE_ACCOUNT` | Зелена за ЧА1, 0 | Вхід доступний, кнопки створення акаунта немає |
| 1 | `appicon/oko/src/main/res/` | Адаптивні, тематичні й звичайні (mdpi–xxxhdpi) значки B4 | Нові файли, 0 | Значок Android 7 і Android 13+ у звичайному й тематичному режимі |
| 1 | `branding/oko/brand.json`, `branding/oko/icon/store_512.png` | затверджена палітра й значок для каталогу | Нові файли, 0 | Звірити палітру з Kotlin, а значок з екранами |
| 1 | `branding/oko/res/drawable/ic_notification.xml` | білий силует сповіщень 24 dp | Новий файл, 0 | Переглянути у шторці сповіщень |
| 1 | `branding/oko/res/drawable*-xxhdpi/onboarding_logo.png` | растровий логотип привітання, світла й темна версії | Нові файли, 0 | Відкрити привітання в обох темах |
| 1 | `features/enterprise/impl-foss/…/OkoEnterpriseService.kt` | Metro, `semanticColorsFlow` і вимкнена прив'язка Matrix; решта контракту делегується. Конструктор повторює залежності `DefaultEnterpriseService`. Якщо Element їх змінить, помилка збирання є очікуваним сигналом оновити `OkoEnterpriseService.kt` | Новий файл, 0 | Збирати FOSS та перевірити тему до й після входу |
| 1 | `libraries/compound/…/colors/OkoPalette.kt` | Копія семантичних токенів акценту в обох темах | Новий файл, 0 | Звірити світлу й темну палітри та контраст |
| 2 | `libraries/compound/…/colors/OkoPalette.kt` | Токени `bgActionPrimary*` та затверджені кольори бульбашок | Новий файл, погоджено в ЧА1 | Перевірити стани кнопок і обидві бульбашки |
| 2 | `libraries/designsystem/…/theme/ColorAliases.kt` | Рядки 14, 42–43, 48–49: два гетери й імпорт | Жовта, 3, погоджено в ЧА1 | Успадковані кольори лишаються в тестах Element. У брендованій темі — зелена своя бульбашка |
| 2 | `app/src/main/res/values/themes.xml` | Рядки 11–12: `windowSplashScreenAnimatedIcon` | Жовта, 1, погоджено 25.11 | Холодний старт у світлій темі |
| 2 | `app/src/main/res/values-night/themes.xml` | Рядки 12–13: `windowSplashScreenAnimatedIcon` | Жовта, 1, погоджено 25.11 | Холодний старт у темній темі |
| 2 | `branding/oko/res/drawable*/oko_splash.xml` | векторний знак холодного старту | Нові файли, 0 | Перевірити центрування й обрізання Android |

Щоб зібрати лише шар 1, прибрати зі своєї гілки записи `bgActionPrimary*` з копії палітри,
відновити два гетери бульбашок і два посилання на прозору заставку з нового тегу Element.
Ресурси OKO можна залишити в дереві. Перед здачею повторити `oko-build`.

## Перевірка і процес

| Файл | Рядки / зміна | Зона та зміни Element за рік | Перевірка |
|---|---|---|---|
| `features/enterprise/impl-foss/…/OkoPaletteTest.kt` | Контраст і збереження еталонної палітри | Новий файл, 0 | `:features:enterprise:impl-foss:testDebugUnitTest` |
| `features/oko-brand-screenshots/` | Окремий Gradle-модуль та знімки Compose обох тем | Нові файли, 0 | `:features:oko-brand-screenshots:recordPaparazziDebug`, після появи еталонів — `verifyPaparazziDebug` |
| `.github/workflows/oko-build.yml` | FOSS APK, тести, знімки й підпис тестового артефакту | Новий файл, 0 | Actions, `oko-build` |
| `.github/workflows/scripts/sign-oko-test-apks.sh` | ЧА1-02. Перевірка режиму й пакета, вибір тестового ключа, звірка підписів обох APK | Новий файл, 0 | У `oko-build` перевірити підпис і артефакт для обох архітектур |
| `docs/oko/zvity/ZVIT_CHA1.md` | Збірка, умови тестування й контраст для власника | Новий файл, 0 | Узгодити з артефактами Actions |
| `.github/workflows/oko-sync-upstream.yml` | Вимкнена підготовка оновлень, окрема гілка, перевірка й чернетка PR | Новий файл, 0 | Після погодження перевірити ручний запуск і обробку конфліктів |

## ЧА3 — Android

| Файли | Зміна | Зона | Перевірка |
|---|---|---|---|
| `branding/oko/paket_android/{icon,onboarding,splash}/` | Вихідні файли B5c з `2a69f5be` | 🟢 ресурси OKO | Збіг із пакетом дизайнера |
| `appicon/oko/src/main/res/`, `branding/oko/icon/store_512.png` | Адаптивний, тематичний, звичайний і круглий значки B5c | 🟢 ресурси OKO | Знімки лаунчера й перевірка APK |
| `branding/oko/res/drawable*/{onboarding_logo.png,oko_splash.xml}` | Логотип входу та вектор заставки у двох темах | 🟢 заміна ресурсів OKO в погоджених слотах | Знімки холодного запуску й входу |
| `docs/oko/PROTOKOL.md`, ЧА3 | Статус Android | 🟢 документація | Звірити з `oko-build` |
| `.github/workflows/oko-build.yml`; `.github/workflows/scripts/{boot-oko-android-emulator.sh,capture-oko-android.py}` | Знімки APK на Android 15, запуск із лаунчера і перевірка системної теми та тематичного значка | 🟢 CI OKO | Артефакт `oko-android-b5c` на push робочої гілки |
| `docs/oko/zvity/ZVIT_CHA3.md` | Звіт ЧА3 і посилання на артефакти | 🟢 документація OKO | Звірити з обома CI-збірками |


## ЧА4 — Android

| Файли | Зміна | Зона | Перевірка |
|---|---|---|---|
| `features/oko-wallpaper/` | Телефонний еталон із архіву `efd4ab01`, асинхронне зменшене декодування, нерухомий фон під стрічкою | 🟢 ізольований модуль OKO | Звірити SHA-256 у готових APK, світлий і темний знімки |
| `features/messages/impl/src/main/kotlin/io/element/android/features/messages/impl/timeline/TimelineView.kt` | Шар 2. Один виклик перед `LazyColumn` у `Box`; лише стрічка кімнати. Element змінював файл 53 рази за рік до `v26.11.1` | 🔴 погоджено о 13:56 | При оновленні перенести виклик перед списком повідомлень; звірити обидві теми й список чатів |
| `features/messages/impl/build.gradle.kts` | Шар 2. Одна залежність на модуль OKO, 14 змін upstream за той самий рік | 🔴 підтримка погодженого виклику | Зберегти залежність при перенесенні виклику |
| `.github/workflows/oko-build.yml` | SHA-256 вихідного й упакованого PNG в обох APK | 🟢 збирання OKO | `oko-build`, журнал перевірки APK |
