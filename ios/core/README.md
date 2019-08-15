# core

В данном проекте содержатся все сервисы, классы, структуры, протоколы, `unit`-тесты и прочее необходимое для работы **Remote Control** приложения (далее **RC**). Здесь **не должны** находится **UI** компоненты или их логика - проект собирается через **SPM**, поэтому он (без дополнительных *костылей*) не в курсе как *такое* собирать.

## Содержание

- [Управление проектом](#project-management)
  - [Тесты](#tests)
  - [Сборка](#build)
  - [Обновление зависимостей](#dependencies-update)
  - [Запуск](#run)

<a name="project-management"></a>

## Управление проектом

Для управления зависимостями **core**-проекта используется [Swift Package Manager (SPM)](https://swift.org/package-manager/) - официальный инструмент от компании `Apple`.

**SPM** не поддерживает **iOS**-сборку, поэтому в **core** не должно содержаться исходных кодов относящихся к **UI** (читай - импортирующих **UIKit**).

Небольшая вводная статья по **SPM** доступна [здесь](https://habr.com/en/company/redmadrobot/blog/348004/).

<a name="tests"></a>

### Тесты

```bash
$> > swift test
Test Suite 'All tests' started at 2019-08-12 10:03:11.775
Test Suite 'RemoteControlPackageTests.xctest' started at 2019-08-12 10:03:11.775
Test Suite 'RemoteControlTests' started at 2019-08-12 10:03:11.775
Test Case '-[RemoteControlTests.RemoteControlTests testExample]' started.
Test Case '-[RemoteControlTests.RemoteControlTests testExample]' passed (0.170 seconds).
Test Suite 'RemoteControlTests' passed at 2019-08-12 10:03:11.945.
         Executed 1 test, with 0 failures (0 unexpected) in 0.170 (0.170) seconds
Test Suite 'RemoteControlPackageTests.xctest' passed at 2019-08-12 10:03:11.945.
         Executed 1 test, with 0 failures (0 unexpected) in 0.170 (0.170) seconds
Test Suite 'All tests' passed at 2019-08-12 10:03:11.945.
         Executed 1 test, with 0 failures (0 unexpected) in 0.170 (0.170) seconds
```

<a name="build"></a>

### Сборка

Собранный `исполняемый`-файл находится по следубщему пути:

.build/**{debug|release}**/**{имя проекта}**

```bash
#
# Обычная сборка, с debug-информацией:
$> swift build  # можно и так: swift build --configuration debug
[2/2] Linking ./.build/x86_64-apple-macosx/debug/RemoteControl
#
# release-сборка
$> swift build --configuration release
[2/2] Linking ./.build/x86_64-apple-macosx/release/RemoteControl
#
# При сборке конкретной цели (target из Package.swift),
# исполняемый файл не создаётся.
# Подобный вид сборки подходит для проверки компилятором
# конкретной цели:
$> swift build --target RemoteControl
[1/1] Compiling Swift Module 'RemoteControl' (1 sources)
#
# Очистить проект:
$> swift package clean
```

<a name="dependencies-update"></a>

### Обновление зависимостей

Если надо обновить зависимости проекта, то необходимо выполнить две следующие команды:

```bash
$> swift package update
...
$> swift package generate-xcodeproj
...
```

Так же, можно воспользоваться скриптом - [update.sh](./update.sh), который можно вызывать из любой папки и он обновит зависимости и сгенерирует всё необходимое для **core**.

<a name="run"></a>

### Запуск

```bash
#
# Запуск уже собранного проекта
$> swift run
Hello, world!
#
# Запуск конкретного бинарного файла, если в проекте пристуствует
# несколько executable в Package.swift
$> swift run RemoteControl
Hello, world!
#
# Так же можно запустить просто бинарный файл:
$> .build/debug/RemoteControl
Hello, world!
```
