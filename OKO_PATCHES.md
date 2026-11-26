# Реєстр змін OKO

Історію пораховано в `upstream/develop` від 26.11.2025 за Києвом, до `277805403f92`.

| Шар | Файл або ресурс | Рядки / зміна | Зона та зміни Element за рік | Перевірка |
|---|---|---|---|---|
| 1 | `plugins/src/main/kotlin/io.element.android-compose-application.gradle.kts` | Імпорт і виклик `configureOkoApplication` | Зелена, 3 | Перевірити `app_name` і значки в APK |
| 1 | `plugins/src/main/kotlin/oko/OkoApplication.kt` | Налаштування назви й джерел ресурсів FOSS | Новий файл, 0 | Перевірити назву кожного варіанта та фінальне злиття ресурсів |
| 1 | `appconfig/…/ApplicationConfig.kt` | `PRODUCTION_APPLICATION_NAME` | Зелена, 0 | Мобільні назви OKO та окрема назва Element для Desktop |
| 1 | `appconfig/…/NotificationConfig.kt` | `NOTIFICATION_ACCENT_COLOR` | Зелена, 0 | Акцент шторки збігається зі світлою палітрою |
| 1 | `appconfig/…/OnBoardingConfig.kt` | `CAN_CREATE_ACCOUNT` | Зелена за ЧА1, 0 | Вхід доступний, кнопки створення акаунта немає |
| 1 | `appicon/oko/src/main/res/` | Адаптивні, тематичні й звичайні (mdpi–xxxhdpi) TEMP-значки | Нові файли, 0 | Значок Android 7 і Android 13+ у звичайному й тематичному режимі |
| 1 | `branding/oko/brand.json`, `branding/oko/icon/store_512.png` | TEMP-палітра й значок для каталогу | Нові файли, 0 | Звірити палітру з Kotlin, а значок з екранами |
| 1 | `branding/oko/res/drawable/ic_notification.xml` | TEMP-силует сповіщень 24 dp | Новий файл, 0 | Переглянути у шторці сповіщень |
| 1 | `branding/oko/res/drawable*/onboarding_logo.xml` | TEMP-логотип привітання, світла й темна версії | Нові файли, 0 | Відкрити привітання в обох темах |
| 1 | `features/enterprise/impl-foss/…/OkoEnterpriseService.kt` | Прив'язка Metro, `semanticColorsFlow` і делегування всіх інших методів | Новий файл, 0 | Збирати FOSS та перевірити тему до й після входу |
| 1 | `libraries/compound/…/colors/OkoPalette.kt` | Копія семантичних токенів акценту в обох темах | Новий файл, 0 | Звірити світлу й темну палітри та контраст |
| 2 | `libraries/compound/…/colors/OkoPalette.kt` | Токени `bgActionPrimary*` та TEMP-кольори бульбашок | Новий файл, погоджено в ЧА1 | Перевірити стани кнопок і обидві бульбашки |
| 2 | `libraries/designsystem/…/theme/ColorAliases.kt` | Два гетери бульбашок та імпорт | Жовта, 3, погоджено в ЧА1 | Успадковані кольори лишаються в тестах Element. У брендованій темі — зелена своя бульбашка |
| 2 | `app/src/main/res/values/themes.xml` | `windowSplashScreenAnimatedIcon` | Жовта, 1, погоджено 25.11 | Холодний старт у світлій темі |
| 2 | `app/src/main/res/values-night/themes.xml` | `windowSplashScreenAnimatedIcon` | Жовта, 1, погоджено 25.11 | Холодний старт у темній темі |
| 2 | `branding/oko/res/drawable*/oko_splash.xml` | TEMP-знак холодного старту | Нові файли, 0 | Перевірити центрування й обрізання Android |

Щоб зібрати лише шар 1, прибрати зі своєї гілки записи `bgActionPrimary*` з копії палітри,
відновити два гетери бульбашок і два посилання на прозору заставку з нового тегу Element.
Ресурси OKO можна залишити в дереві. Перед здачею повторити `oko-build`.
