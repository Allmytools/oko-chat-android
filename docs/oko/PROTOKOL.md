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
