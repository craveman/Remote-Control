
# iOS Remote Control

Здесь находятся файлы связанные с **iOS**-версией продукта **Remote Control** (далее **RC**).

## Содержание

- [Структура проекта](#project-structure)
  - [Зачем так?](#why)
- [Управление проектом](#project-management)
  - [Тесты](#tests)
  - [Сборка](#build)
  - [Обновление зависимостей](#dependencies-update)
  - [Запуск](#run)

<a name="project-structure"></a>

## Структура проекта

Высокоуровневое описание структуры проекта:

- **core** - здесь находится основное ядро проекта, необзодимые сервисы и службы, под управлением [Swift Package Manager (SPM)](https://swift.org/package-manager/) - официальный инструмент от компании `Apple` для работы с зависимостями и управления жизненным циклом сборки проекта;

- **app** - **Xcode**-проект, содержащий код относящийся к *визуальной* составляющей приложения **RC** под операционную систему **iOS**;

- **workspace.xcworkspace** - *зонтичный* проект, позволяющий открывать выше описанные проекты в **Xcode** одновременно,

> **ВАЖНО**: Перед первым открытием проекта в **Xcode**, необходимо сгенерировать в **core** необходимую структуру для **Xcode**:
>
> ```bash
> $> cd core; swift package generate-xcodeproj
> ...
> ```
>
> Или просто вызвать скрипт [update.sh](./update.sh), который обновит зависимости и, заодно, сгенерирует всё необходимое.

<a name="why"></a>

### Зачем так?

Пытливый читатель может задаться вопросом - **А нафига два проекта, а не один?** И я тебе отвечу, мой милый друг:

Компания `Apple` предоставила удобную утилиту - **SPM**, благодаря которой довольно легко управлять проектом - собирать тестовые/релизные конфигурации, прогонять тесты, скачивать зависимости и многое другое. Но, наверное по какому-то трагическому стечению обстоятельств или банальной рассеянности, компания забыла добавить поддержку **iOS**-сборок. Видимо **iOS** платформа не является чем-то приоритетным для компании, поэтому этот вопрос немного упустили из виду.

Иными словами - собрать **iOS**/**iWatch**/**tvOS**-проекты можно в **SPM**, но сложно - вместо простого:

```bash
$> swift build
...
$> swift test
...
```

придётся писать нечто подобное (подробнее про это [тут](https://dive.github.io/swift-package-manager/ios/2019/01/20/swift_package_manager_vs_ios.html)):

```bash
$> swift build \
    -Xswiftc "-sdk" \
    -Xswiftc "`xcrun --sdk iphonesimulator --show-sdk-path`" \
    -Xswiftc "-target" \
    -Xswiftc "x86_64-apple-ios12.4-simulator"
...
$> swift test --verbose \
    -Xswiftc "-sdk" \
    -Xswiftc "`xcrun --sdk iphonesimulator --show-sdk-path`" \
    -Xswiftc "-target" \
    -Xswiftc "x86_64-apple-ios12.4-simulator" \
    -Xswiftc "-lswiftUIKit"
...
```

Конечно можно обернуть все подобные вызовы в некий `shell`-скрипт и вызывать его, но больше похоже на костыль а не решение и это был бы достаточно сложный скрипт - достаточно обратить внимание на то что указывается какая то конкретная версия эмулятора, т.е. нужно узнать какая есть на данной машине доступная версия, поставить её ну и т.д и т.п.

Так же, можно было бы использовать альтернативы:

- [CocoaPods](https://cocoapods.org);
- [Carthage](https://github.com/Carthage/Carthage).

Сравнение менеджеров зависимостей доступно [здесь](https://medium.com/xcblog/swift-dependency-management-for-ios-3bcfc4771ec0).

Наверное использование альтернатив несколько бы упростило задачу сейчас, но есть одно **но** - **SPM** - проект от компании `Apple`, он, хоть и молодой (появился в 2015 году), но очень активно развивается и рано или поздно станет стандарт де-факто для разработки на **Swift**, соответственно данные *неудобства* - временные и тратить время на изучение других инструментов, в долгосрочной перспективе, не выгодно.

Поэтому текущее решение следующее:

- все необходимые сервисы, классы, структуры, протоколы и прочее - засунуть в проект под управлением **SPM**, там где нужно использовать сторонние зависимости, писать и запускать `unit`-тесты. Это и есть содержимое проекта **core**;

- пользовательский интерфейс, собственно с которым и существуют проблемы при сборке, вынести в отдельный **Xcode**-проект - **app**. Он должен открываться, собираться и тестироваться только средствами **Xcode**, а в качестве единственной зависимости у него - **core**-проект. Здесь находится всё что относится к пользовательскому интерфейсу (читай - всё что импортирует **UIKit**), сториборда, файлы локализации, настройки и прочее;

- что бы было удобно открывать данный проект в **Xcode** - создан *зонтичный* проект - **workspace.xcworkspace**, который просто содержит информацию о том как открыть оба проекта выше в одном инстансе **Xcode**.

> **ВАЖНО**: После добавления зависимостей в **core**-проект, необходимо выполнять следующие команды:
>
> ```bash
> $> swift package update
> ...
> $> swift package generate-xcodeproj
> ...
> ```
>
> Или просто вызвать скрипт [update.sh](./update.sh), который выполняет команды выше.

<a name="project-management"></a>

## Управление проектом

Здесь описано взаимодействие только с **core**-проектом, так как любое обслуживание **app** производится из **Xcode** и не является чем то особенным и требующим пояснений.

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
