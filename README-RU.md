# Ramus Remaster — документация на русском

**Современный форк [Ramus](https://github.com/Vitaliy-Yakovchuk/ramus)** — Java-редактора бизнес-процессов в нотациях IDEF0 и DFD.

## Что изменено по сравнению с оригиналом

| Область | Оригинал | Remaster |
|---|---|---|
| JDK | 8 | 21 (Temurin) |
| Интерфейс | системный L&F (Metal/Windows) | FlatLaf 3.8, светлая и тёмная темы |
| H2 (встроенная БД .rsf) | 1.3.163 | 2.5.252 |
| POI / iText / JFreeChart | 3.13 / 5.5.7 / 1.0.19 | 5.5.1 / 5.5.13.6 / 1.5.6 |
| Модули сборки | 28 (включая сервер, WebStart, izpack) | 18 — только локальный редактор |
| Тесты формата .rsf | нет | round-trip на всех образцах + операции модели |

### Совместимость с .rsf

- **Открытие**: remaster открывает все файлы оригинала 2.x, а также старые файлы
  эпохи Ramus 1.x. Если файл ссылается на плагины, которых в remaster нет
  (например `Attribute.Doc.Way`), они пропускаются с предупреждением в лог:
  `[Ramus] File requires unregistered plugins [...]`. Данные таких плагинов при
  сохранении теряются — сохраняйте такие файлы под новым именем.
- **Сохранение**: файлы, сохранённые remaster, открываются в оригинальном
  приложении (проверено вручную; см. `docs/baseline/t41_original_opens_remaster_saved.png`).

Подробности расследования багов открытия — в [`docs/rsf-load-bugs.md`](docs/rsf-load-bugs.md).

## Скачать и установить

Готовые сборки прикрепляются к [релизам](https://github.com/UwerfiU/ramus-remaster/releases)
при каждом пуше тега `v*` (собирает GitHub Actions). Все сборки содержат
собственный Java-рантайм — устанавливать JDK не нужно.

### Windows

1. Скачайте `Ramus-0.1.0.msi` и запустите: установка per-user, ярлык в меню Пуск,
   ассоциация файлов `.rsf`. Удаление — через «Приложения и возможности».
2. Портативная версия без установки (локальная сборка):
   ```bat
   gradlew.bat winPackage -PpackagingWinType=app-image
   ```
   → папка `dest\windows\Ramus\`, запускается `Ramus.exe`.

### macOS

1. Скачайте DMG под свой процессор: Apple Silicon (M1/M2/…) — `...-arm64.dmg`,
   Intel — `...-x86_64.dmg`.
2. Откройте DMG и перетащите **Ramus** в **Applications**.
3. Приложение не подписано, поэтому при первом запуске macOS заблокирует его:
   правый клик по Ramus в Finder → **Open** → **Open**, либо один раз выполните
   `xattr -dr com.apple.quarantine /Applications/Ramus.app`.

### Linux (x86_64)

- Портативная версия (любой дистрибутив): распакуйте
  `ramus-portable-linux-x86_64.tar.gz` и запустите `Ramus/bin/Ramus`
  (удобно сделать symlink в `~/bin`).
- Debian/Ubuntu: `sudo dpkg -i ramus_*.deb`
- Fedora/RHEL: `sudo rpm -i ramus-*.rpm` (или `dnf install ./ramus-*.rpm`)

## Запуск из исходников

Требуется JDK 21 (например, Temurin). На Windows:

```bat
gradlew.bat runLocal
gradlew.bat runLocal -Popen="C:\path\to\model.rsf"
gradlew.bat runLocal -Ptheme=dark
```

Linux/macOS: `./gradlew ...` с теми же аргументами.

## Сборка и тесты

```bash
./gradlew build                          # компиляция + все тесты
./gradlew test                           # только тесты
```

## Упаковка

```bash
# Windows: портативная версия (папка с Ramus.exe, без установки; WiX не нужен)
./gradlew winPackage -PpackagingWinType=app-image
# → dest/windows/Ramus/  (Ramus.exe + app + runtime)

# Linux: портативная папка / deb / rpm — только на Linux (jpackage)
./gradlew linuxPackage -PpackagingLinuxType=app-image   # → dest/linux/Ramus/
./gradlew linuxPackage -PpackagingLinuxType=deb         # → dest/linux/ramus_*.deb
./gradlew linuxPackage -PpackagingLinuxType=rpm         # → dest/linux/ramus-*.rpm
```

Инсталляторы собирает GitHub Actions при пуше тега вида `v0.1.0`:

- Windows MSI — `.github/workflows/windows-installer.yml` (собирает, ставит,
  проверяет запуск и удаление, прикрепляет артефакт к релизу);
- macOS DMG (arm64 + x86_64) — `.github/workflows/macos-dmg.yml`;
- Linux (портативная папка + deb + rpm) — `.github/workflows/linux-packages.yml`
  (включая smoke-тест запуска под xvfb).

## Структура репозитория (кратко)

- `local-client` — точка входа (`com.ramussoft.local.Main`), лаунчер, задачи запуска/упаковки;
- `core`, `data-framework-common`, `database-storage` — движок и формат .rsf;
- `gui-*` — Swing-интерфейс (FlatLaf);
- `idef0-*` — нотация IDEF0;
- `docs/` — документация: карта модулей, баги загрузки, скриншоты.

## Лицензия

GNU GPL v3, как и оригинал. Оригинальная работа — Vitaliy Yakovchuk.
