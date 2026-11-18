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
# Портативная версия (папка с Ramus.exe, без установки; WiX не нужен)
./gradlew winPackage -PpackagingWinType=app-image
# → dest/windows/Ramus/  (Ramus.exe + app + runtime)

# Инсталлятор MSI — только через GitHub Actions: пуш тега вида v0.1.0
# запускает .github/workflows/windows-installer.yml, который собирает MSI,
# проверяет установку/запуск/удаление и прикрепляет артефакты к релизу.
```

## Структура репозитория (кратко)

- `local-client` — точка входа (`com.ramussoft.local.Main`), лаунчер, задачи запуска/упаковки;
- `core`, `data-framework-common`, `database-storage` — движок и формат .rsf;
- `gui-*` — Swing-интерфейс (FlatLaf);
- `idef0-*` — нотация IDEF0;
- `docs/` — документация: карта модулей, баги загрузки, скриншоты.

## Лицензия

GNU GPL v3, как и оригинал. Оригинальная работа — Vitaliy Yakovchuk.
