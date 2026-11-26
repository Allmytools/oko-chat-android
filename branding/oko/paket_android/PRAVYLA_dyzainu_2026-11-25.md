# ОКО Чат (Android) — правила дизайну (копія документа проєкту `claude/OKO_chat_dyzain_pravyla_2026-11-25.md`)

> Копія для виконавця, який не має доступу до документів проєкту. Джерело правди — документ проєкту; при розбіжності
> діє він. Назва застосунку з 25.11 — **OKO** (латиницею), а не «ОКО Чат».

**Коротко**
- Застосунок — копія Element X для Android з нашою назвою, значком, логотипом і кольором. Функції не змінюються.
- Міняємо **картинки, назву й кольори**. Не міняємо **будову екранів** (розташування, кнопки, меню, шрифти).
- Element X має «гнізда» для брендування: логотип екрана привітання підставляється без зміни коду (`onboarding_logo`),
  палітра — через одне місце (`semanticColorsFlow`).
- Затверджує власник за знімками **справжньої збірки** в світлій і темній темах.

Підстава: код `element-hq/element-x-android` станом на 25.11.2026 (коміт `277805403f92`, після v26.11.1).

## 2. Головний принцип: ресурси й значення, а не код екранів

| Місце | Що там | Змін за рік | Висновок |
|---|---|---|---|
| `appicon/element/…` | шари значка | 0 | своє — окремим модулем `appicon/oko` |
| `libraries/designsystem/…/drawable/ic_notification.xml` | значок сповіщень | 0 | заміна ресурсом з тим самим ім'ям |
| `OnBoardingLogoResIdProvider.kt` | шукає ресурс `onboarding_logo` | 0 | логотип додається файлом, код не чіпаємо |
| `appconfig/…/ApplicationConfig.kt` | назва в текстах | 0 | 1–2 рядки |
| `appconfig/…/NotificationConfig.kt` | колір сповіщень | 0 | 1 рядок |
| `app/src/main/res/values/colors.xml` | фон заставки | 0 | не чіпати |
| `app/src/main/res/values/themes.xml` | заставка | 1 | жовта зона |
| `libraries/designsystem/…/theme/ColorAliases.kt` | колір бульбашок | 2 | жовта зона, 2 рядки |
| `libraries/compound/…/theme/ElementTheme.kt` | тема | 6 | **не чіпати** |
| `libraries/compound/…/tokens/generated/` | палітра Element | 7 імпортів | **не чіпати** (`DO_NOT_MODIFY`) |
| `plugins/…/config/BuildTimeConfig.kt` | назва, ідентифікатор | 8 | лише значення констант |
| `features/enterprise/impl-foss/…/DefaultEnterpriseService.kt` | гніздо палітри | 11 | мінімум рядків, своє — в новому файлі |
| `features/login/…/onboarding/OnBoardingView.kt` | екран привітання | 15 | **не чіпати** |
| `app/build.gradle.kts` | збирання | 28 | 1–2 рядки |

## 3. Зелена зона
Назва (`BuildTimeConfig.APPLICATION_NAME`, `ApplicationConfig.PRODUCTION_APPLICATION_NAME`); значок застосунку (модуль
`appicon/oko`); шар `monochrome`; `ic_notification`; `onboarding_logo` (+ `drawable-night/`); колір акценту через
`semanticColorsFlow`; `NotificationConfig.NOTIFICATION_ACCENT_COLOR` = акцент світлої теми.
Коли ресурс `onboarding_logo` знайдено, стандартний логотип, фон **і вітальний текст** Element зникають.

## 4. Жовта зона (лише після погодження інтегратора)
Основні кнопки (`bgActionPrimaryRest/Hovered/Pressed/Disabled`); колір бульбашок (`ColorAliases.kt`:
`messageFromMeBackground`, `messageFromOtherBackground`); значок на заставці (`themes.xml`,
`windowSplashScreenAnimatedIcon`); кольори успіху (`*Success*`, за замовч. не міняємо); `textLinkExternal`,
`borderFocused` (за замовч. не міняємо).

## 5. Червона зона
Розташування, розміри, нові/прибрані кнопки, вкладки, меню, екрани; свій шрифт; кути, відступи, тіні, анімації; свої
значки інтерфейсу; правка текстів інтерфейсу (крім назви); будь-яка правка в `tokens/generated/`, `ElementTheme.kt`,
`OnBoardingView.kt`; колір фону екранів; назва чи логотип Element; версія для iPhone; зміна функцій.

## 6. Технічні вимоги до файлів
**6.1 Значок:** три шари 108×108 dp (`foreground`, `background` без прозорості, `monochrome`); усе важливе в колі 66 dp;
растр `foreground`/`monochrome` 108/162/216/324/432 px; `ic_launcher` і `ic_launcher_round` 48/72/96/144/192 px;
512×512 для F-Droid.
**6.2 Сповіщення:** одноколірний силует на прозорому, 24×24 dp, впізнається з ~16 px.
**6.3 Привітання:** ресурс рівно `onboarding_logo`, `drawable/` і `drawable-night/`; показ по центру у власному розмірі,
поля 16 dp; растр xxhdpi ~600×600 px; містить знак і назву.
**6.4 Палітра:** два набори (світла, темна; чорна береться з темної). Контраст: `textActionAccent` на фоні ≥ 4,5 : 1;
`textOnSolidPrimary` на `bgAccentRest` і `bgActionPrimaryRest` ≥ 4,5 : 1; значки й рамки ≥ 3 : 1; стани Rest → Hovered →
Pressed розрізняються на око. Жодного кольору поза переліком.

## 9. Як пакет лягає в код
- Модуль `appicon/oko` за зразком `appicon/element` + 1 рядок у `app/build.gradle.kts`.
- Тека ресурсів бренду, підключена до модуля `app`, з `ic_notification` та `onboarding_logo`: ресурси `app` перекривають
  однойменні ресурси бібліотек, файли Element не змінюються.
- Палітра: новий файл із `SemanticColors.copy(…)` лише для токенів з `brand.json`; у
  `DefaultEnterpriseService.semanticColorsFlow` — один рядок (або заміна через DI — оцінює виконавець).
- Назва й колір сповіщень — значення констант.
- Стандартні знімки-тести Element не перезаписувати — окремий набір знімків бренду.
- Якщо токен із `brand.json` зник у новому Element — збирання має впасти; це сигнал оновити `brand.json`.

## 10. Ліцензія і марка
Код — AGPL-3.0: зберігати заголовки й `LICENSE`, код форка доступний користувачам. Марки Element у назві й логотипі не
використовувати; можна зазначити «побудовано на відкритому коді Element, офіційно не пов'язано». Джерело:
https://element.io/legal/trademark-policy
