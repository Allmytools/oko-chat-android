# OKO (Android) — протокол завдань

Записи нові — внизу розділу. Час — Київ.

## ЧА1 — збірка застосунку OKO, перша тестова версія
Завдання: `docs/oko/zavdannia/CHA1_zbirka.md`. Виконавець: Codex. Гілка: `ai/oko-brand` → PR у `oko/main`.

- **26.11.2026 07:25 · Інтегратор.** Етап Е0 зроблено: гілка `oko/main` = тег Element `v26.11.1` (`3a61e4fd`) +
  документи `docs/oko/`. Пакет бренду дизайнера ще не покладено в `branding/oko/` — до того працюй на `TEMP`-зображеннях.
  Відкриті рішення власника: ключ підпису (секрети GitHub), увімкнення Actions, домен сервера Matrix,
  обов'язковий PIN-код (`LockScreenConfig.IS_PIN_MANDATORY`) — поки **не робити**.
- **26.11.2026 07:35 · Інтегратор.** Власник увімкнув Actions у репозиторії. Доступ Codex до репозиторію налаштовано
  (ChatGPT Codex Connector, читання й запис). Перший крок Е0/Е3: перевір, які workflow Element тепер запускаються в нашій
  копії за розкладом чи на push, і вимкни ті, що потребують чужих секретів або сервісів (див. завдання, Е0).

- **26.11.2026 07:38 · Виконавець.** Почато Е1 з TEMP. Чекаю остаточний `branding/oko/`: палітру, адаптивний/тематичний значок,
  значок шторки, логотип привітання і знак заставки у світлій і темній темах, значок каталогу й таблицю контрасту.
  Жовті погоджені позиції: кнопки через палітру, два гетери бульбашок (3 зміни Element за рік), дві теми заставки
  (по 1 зміні). Ризик оновлення — конфлікт у місцях підключення та повторна перевірка контрасту.
  `BuildTimeConfig.kt` (8 змін), `settings.gradle.kts` (14), `app/build.gradle.kts` (28) і
  `DefaultEnterpriseService.kt` (11) мають підвищений ризик. Назва й джерела ресурсів додаються через FOSS-хук
  у convention-плагіні (3 зміни); `appicon/oko` під'єднано як окреме джерело ресурсів, без нового Gradle-проєкту.
  Палітру підставляє заміна Metro в новому файлі з делегуванням штатному FOSS-сервісу.
  SDK локально відсутній; базова команда Gradle Wrapper зупинилася на завантаженні Gradle: `Network is unreachable`.
- **26.11.2026 07:38 · Виконавець.** Перевірено тригери workflow Element. Шість workflow-розкладів уже мають умову
  `element-hq/element-x-android`: `gradle-wrapper-update`, `nightly`, `nightlyReports`,
  `sync-enterprise-submodule`, `sync-localazy` і `sync-sas-strings`. `post-release` також
  захищено умовою репозиторію. Через вкладку Actions → Disable workflow для цього форка слід вимкнути
  `build` (Diawi), `build_enterprise` (приватний підмодуль), `maestro-local` (обліковки й зовнішні сервіси),
  `sonar`, `tests` (Codecov), `quality` (enterprise), `triage-incoming` і `triage-labelled`
  (бот Element), `pull_request` і `pr-checks` (автоматизація PR Element), `release` (сервіси/ключі Element) і
  `stale-issues` (нічний тріаж). Перелік для власника; стан вимкнення у GitHub поки не підтверджено.
- **26.11.2026 07:41 · Виконавець.** Е2: FOSS release за замовчуванням має `com.interatletika.oko.test` і назву «OKO test»;
  `-Poko.test=false` вибирає `com.interatletika.oko`. Власні схеми OAuth розрізняють версії; вхід через MAS
  потрібно перевірити на APK із затвердженим Matrix-сервером. Порожня `OKO_HOMESERVER` залишає вибір сервера.
  Для прив'язки після погодження вписати Matrix server name (домен, який бачить користувач) у цю константу
  в `OkoEnterpriseService.kt` і перевірити вхід на дозволеному та сторонньому серверах.
  Робочий ключ створює власник/IT локально:
  `keytool -genkeypair -keystore oko-release.p12 -storetype PKCS12 -alias oko-release -keyalg RSA -keysize 3072 -validity 10000`.
  Паролі вводити у запрошеннях `keytool`. Зберегти дві зашифровані резервні копії сховища та відбиток сертифіката;
  Base64 сховища й паролі покласти тільки в GitHub Actions secrets `OKO_SIGNING_STORE_BASE64`,
  `OKO_SIGNING_STORE_PASSWORD`, `OKO_SIGNING_KEY_ALIAS`, `OKO_SIGNING_KEY_PASSWORD`.
  Для `.test` використати окреме сховище й окремі секрети з префіксом `OKO_TEST_SIGNING_`; ключі між версіями
  не взаємозамінні. Секрети не додавати до репозиторію або повідомлень.
- **26.11.2026 07:46 · Виконавець.** Е3: додано `oko-build` і окремий модуль знімків світлої й темної тем.
  Android SDK 37.0 локально встановлено. Розрахований TEMP-контраст часу на темній зеленій бульбашці — 5,06:1.
  Звіт з умовами підпису, контрастом і чернеткою випуску — [ZVIT_CHA1.md](zvity/ZVIT_CHA1.md).
- **26.11.2026 07:47 · Виконавець.** Е4: підготовлено `oko-sync-upstream` з вимкненим запуском і без активного розкладу.
  Додано інструкцію для власника про встановлення APK, перевірку помилок оновлення та Obtainium.
- **26.11.2026 07:48 · Виконавець.** Перший [прогін oko-build](https://github.com/Allmytools/oko-chat-android/actions/runs/45763779932)
  зупинився на `sdkmanager: command not found` у хмарного runner-а. Додано фіксовану версію
  `android-actions/setup-android`, встановлення SDK 37.0 та окрему перевірку двох тестових APK до підпису.
