# Ramus Remaster

**Современный фок хуйхуйхуйхуйхуйхуйхуйхуйхуйхуйхуйхуй [Ramus](https://github.com/Vitaliy-Yakovchuk/ramus)** — Java-редактора бизнес-процессов в нотациях IDEF0 и DFD.

A modernized fork of **Ramus**, a Java-based IDEF0 & DFD business-process modeller.

| | |
|---|---|
| Русский | [README-RU.md](README-RU.md) |
| English | this file, sections below the Russian summary |

## Что в remaster / What's new

- **JDK 21** (оригинал — JDK 8) / modernized toolchain
- **FlatLaf 3.8** — современный вид интерфейса, светлая и тёмная темы (переключатель: *View → Toggle light/dark theme*)
- **Обновлены зависимости**: POI 5.5, OpenPDF (вместо AGPL iText), JFreeChart 1.5, H2 2.5
- **Совместимость с .rsf в обе стороны**:
  - remaster открывает старые файлы, включая эпохи Ramus 1.x (отсутствующие плагины пропускаются с предупреждением в лог);
  - файлы, сохранённые remaster, открываются в оригинальном приложении.
- **Упаковка**: портативная сборка (`app-image`), Windows EXE (мастер: каталог + ярлыки) и MSI (тихая установка) через CI.

![Light theme](docs/baseline/t58_main_light.png)
*Светлая тема / Light theme*

![Dark theme](docs/baseline/t58_main_dark.png)
*Тёмная тема / Dark theme (View → Switch light/dark theme)*

## Установка / Installation

Готовые сборки появляются на странице [releases](https://github.com/UwerfiU/ramus-remaster/releases)
при каждом пуше тега `v*`. Все сборки содержат собственный Java-рантайм —
JDK на компьютере пользователя не нужен. / Ready-made builds are attached to
[releases](https://github.com/UwerfiU/ramus-remaster/releases) on every `v*` tag;
each one bundles its own Java runtime, so no JDK is needed on the user's machine.

### Windows

1. Рекомендуемый вариант — установщик `Ramus-<версия>.exe`: мастер установки
   позволяет выбрать каталог установки и поставить/снять галки ярлыков
   (рабочий стол и меню Пуск). Установка per-user, без прав администратора,
   ассоциация файлов `.rsf`. Удаление — через «Приложения и возможности».
2. `Ramus-<версия>.msi` — тот же продукт в виде MSI (для тихой установки:
   `msiexec /i Ramus.msi /qn`), без мастера выбора параметров.
3. Портативная версия без установки (собирается локально):
   ```bat
   gradlew.bat winPackage -PpackagingWinType=app-image
   ```
   → папка `dest\windows\Ramus\`, запускается `Ramus.exe`.

1. Recommended: the `Ramus-<version>.exe` installer — its wizard lets you choose
   the install directory and tick/untick the shortcuts (desktop and Start menu).
   Per-user install, no administrator rights, `.rsf` file association. Uninstall
   via "Apps & features".
2. `Ramus-<version>.msi` — the same product as an MSI (for silent installs:
   `msiexec /i Ramus.msi /qn`), without the wizard options.
3. Portable version without installation (built locally):
   ```bat
   gradlew.bat winPackage -PpackagingWinType=app-image
   ```
   → `dest\windows\Ramus\` folder, run `Ramus.exe`.

### macOS

1. Скачайте DMG под свой процессор: Apple Silicon (M1/M2/…) — `...-arm64.dmg`,
   Intel — `...-x86_64.dmg`.
2. Откройте DMG и перетащите **Ramus** в **Applications**.
3. Приложение не подписано, поэтому при первом запуске macOS заблокирует его:
   правый клик по Ramus в Finder → **Open** → **Open**
   (либо один раз выполните `xattr -dr com.apple.quarantine /Applications/Ramus.app`).

1. Download the DMG for your chip: Apple Silicon (M1/M2/…) — `...-arm64.dmg`,
   Intel — `...-x86_64.dmg`.
2. Open the DMG and drag **Ramus** into **Applications**.
3. The app is unsigned, so macOS blocks it on first launch: right-click Ramus in
   Finder → **Open** → **Open** (or run
   `xattr -dr com.apple.quarantine /Applications/Ramus.app` once).

### Linux

- Портативная версия (любой дистрибутив, x86_64): распакуйте
  `ramus-portable-linux-x86_64.tar.gz` и запустите `Ramus/bin/Ramus`
  (удобно сделать symlink в `~/bin`).
- Debian/Ubuntu: `sudo dpkg -i ramus_*.deb`
- Fedora/RHEL: `sudo rpm -i ramus-*.rpm` (или `dnf install ./ramus-*.rpm`)

- Portable build (any x86_64 distro): unpack
  `ramus-portable-linux-x86_64.tar.gz` and run `Ramus/bin/Ramus`
  (a symlink in `~/bin` is convenient).
- Debian/Ubuntu: `sudo dpkg -i ramus_*.deb`
- Fedora/RHEL: `sudo rpm -i ramus-*.rpm` (or `dnf install ./ramus-*.rpm`)

## Запуск из исходников / Run from source

Требуется JDK 21. / Requires JDK 21.

```bash
./gradlew runLocal                      # запуск приложения
./gradlew runLocal -Popen="path/to/model.rsf"   # открыть файл сразу
./gradlew runLocal -Ptheme=dark         # тёмная тема
```

## Сборка / Building

```bash
./gradlew build                         # compile + все тесты / all tests
./gradlew winPackage -PpackagingWinType=app-image   # портативная версия → dest/windows/Ramus/
./gradlew linuxPackage -PpackagingLinuxType=app-image  # только на Linux → dest/linux/Ramus/
```

Инсталляторы (Windows MSI, macOS DMG, Linux deb/rpm) собирает GitHub Actions
при пуше тега `v*`; артефакты прикрепляются к релизу.

Installers (Windows MSI, macOS DMG, Linux deb/rpm) are built by GitHub Actions
on `v*` tags and attached to the release.

## Формат файлов / Project format

Модели хранятся в `.rsf` — ZIP-архив с XML-таблицами и метаданными.
Формат полностью совместим с оригинальным Ramus 2.x (и с файлами 1.x при
открытии). Подробности — в `docs/rsf-load-bugs.md`.

Models are stored in `.rsf` — a ZIP archive of XML tables and metadata, fully
compatible with original Ramus 2.x (and readable from 1.x-era files).

## Лицензия / License

GNU GPL v3, как и оригинал. / Same as the original: GNU GPL v3.
Original work by Vitaliy Yakovchuk.

Зависимости совместимы с GPL: OpenPDF (LGPL) вместо AGPL iText,
FlatLaf (LGPL), H2 (MPL/EPL). Приложение можно распространять как GPLv3.
/ Dependencies are GPL-compatible: OpenPDF (LGPL) instead of AGPL iText,
FlatLaf (LGPL), H2 (MPL/EPL). The application can be distributed as GPLv3.
