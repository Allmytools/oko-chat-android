# ЧА3 — значок B5c, анімація інсталятора й шпалери

| Платформа | Що перевірити | Артефакти |
|---|---|---|
| Windows x64 | Знак B5c, анімація інсталятора, логотипи входу й домашньої, шпалери тільки за повідомленнями в темах OKO | `oko-desktop-windows-test`, `oko-desktop-screenshots`. [Збірки Windows](https://github.com/Allmytools/oko-chat-android/actions/workflows/oko-desktop-build.yml?query=branch%3Aai%2Foko-desktop) |
| Android | Значок B5c у лаунчері, зокрема тематичний, системна заставка, вітання перед входом. Обидві теми | `oko-android-b5c`, `oko-brand-screenshots`, підписані тестові APK. [Збірки Android](https://github.com/Allmytools/oko-chat-android/actions/workflows/oko-build.yml?query=branch%3Aai%2Foko-brand) |

Windows використовує пакет дизайнера `5cbe9e54`, Android — `2a69f5be`. На Android вихідні SVG збережено, їхні геометричні примітиви перенесено у VectorDrawable. Шари растрових значків і логотипи взято з пакета. Ресурс сповіщень лишився з ЧА1.

## Windows

- Під час збирання B5c підставляється у значки Electron, логотипи — у вебпакет, а GIF — у `build/install-spinner.gif`. Перевірка дістає `background.gif` із ZIP-ресурсу самого `Setup.exe` і порівнює його байти з ресурсом дизайнера.
- У темах OKO є по одному кольоровому маркеру. Element переносить його в inline-змінну на `body` і прибирає при перемиканні теми. CSS використовує маркер і пару селекторів стрічки. Світла тема OKO отримує `#F4F0E9` та світлий SVG, темна — `#101317` і темний SVG. Обидва SVG є локальними файлами збірки. У вбудованих темах Element маркерів немає.
- Workflow перевіряє селектори у двох CSS-файлах офіційного вебпакета. У Chromium додатково перевіряються фактична структура екрана, завантаження SVG, фон стрічки, поле введення, бічна панель і відсутність шпалер у вбудованих світлій, темній та висококонтрастних темах. `chat-element-light.png` — контрольний знімок.
- Щоб побачити шпалери, у «Налаштування → Вигляд» вимкніть «Як у системі» й оберіть «OKO світла» або «OKO темна». Після зміни `desktop/ELEMENT_WEB_TAG` збірка впаде, якщо Element прибрав потрібний селектор або змінив поведінку тем. Тоді потрібно звірити CSS і повторити знімки. Код екранів Element лишився без правок.

## Android

Для тегу Element X `v26.11.1` штатного слота для шпалер стрічки не знайшов. У `TimelineView` список Compose малюється у `Box` з `LazyColumn`, а `MessagesView` не передає зображення чи ресурс тла. Палітра може задати кольори інтерфейсу, однак не встановлює зображення лише за повідомленнями. Шпалери Android потребували б окремої зміни екрана. [TimelineView.kt](https://github.com/element-hq/element-x-android/blob/v26.11.1/features/messages/impl/src/main/kotlin/io/element/android/features/messages/impl/timeline/TimelineView.kt#L204-L223) · [MessagesView.kt](https://github.com/element-hq/element-x-android/blob/v26.11.1/features/messages/impl/src/main/kotlin/io/element/android/features/messages/impl/MessagesView.kt#L640-L672)
