# core

В данном проекте содержатся все сервисы, классы, структуры, протоколы, `unit`-тесты и прочее необходимое для работы **Remote Control** приложения (далее **RC**). Здесь **не должны** находится **UI** компоненты или их логика - проект собирается через **SPM**, поэтому он (без дополнительных *костылей*) не в курсе как *такое* собирать.

## Содержание

- [Компоненты](#components)
  - [networking](#networking)
  - [logging](#logging)
  - [sm02](#sm02)
  - [utils](#utils)
- [Управление проектом](#project-management)
  - [Тесты](#tests)
  - [Сборка](#build)
  - [Обновление зависимостей](#dependencies-update)
  - [Запуск](#run)

<a name="components"></a>

## Компоненты

Далее описаны различные копоненты, содержащиеся в проекте `core`.

<a name="networking"></a>

### networking

Данный модуль, содержит код позволяющий *общаться* с сервером **SM-02**.

Пример использования:

```swift
import networking


NetworkManager.shared
    // обработчик входящих сообщений от сервера
    .handle(messages: { inbound in
      switch inbound {
      case let .broadcast(weapon, left, right, timer, timerState):
        print("a broadcast request from server")
      case let .genericResponse(request) where request == 0x01: // ответ на setName
        print("the name message (tag=\(request)) was set")
      default:
        print("unsupported inbound message \(inbound)")
      }
    })
    // мы можем обрабатывать сообщения по одному
    .on(events: { event in
      if case .pingCatched(_) = event {
        print("ping catched")
      }
    })
    // или сразу несколько в одном обработчике
    .on(events: { event in
      switch event {
      case .connectionReadTimeout:
        print("connection to server was lost")
      case let .pingCatched(serverHost):
        print("found a remote server \(serverHost)")
      default:
        return
      }
    })
    // запускаем сервис, который будет работать в отдельном потоке
    // так-же, можно остановить, снова запустить и отключить сервис
    .start()

// отправляем сообщения на сервер
let request = Outbound.setName(person: .right, name: "Artem")
NetworkManager.shared.send(message: request)
```

Входящие (**Inbound**) и исходящие (**Outbound**) сообщения описаны в [файле](./Sources/networking/Messages.swift).

Тест, который показывает сценарий использования `NetworkManager` и *симулятора* `SM-02` находится [здесь](./Tests/sm02/IntegrationTests.swift).

<a name="logging"></a>

### logging

Для логгирования различных событий можно использовать модуль логгирования.

Можно указать конкретное имя логгера, но, по умолчанию, это будет имя файла, от куда был вызван метод **create()**:

```swift
import logging


let logger = LoggerFactory.create()

logger.info("hello")
```

Каждый логгер имеет поле `context` и несколько параметров в нём, которые могут быть настроены пользователем:

```swift
// Указать уровень логгирования для логгера.
// По умолчанию логгеры имеют уровень логгирования их предка
logger.context.logLevel = .DEBUG

// Форматирование даты логгируемого сообщения.
// По умолчанию это: yyyy-MM-dd HH:mm:ss.SSS
logger.context.dateFormatter = "MMM dd,yyyy"

// Настройка формата сообщения лога
logger.context.outputLogFormat = "${:green}${LEVEL} ${:default}${MESSAGE}"
```

Все логгеры имеют родительский контекст, по умолчанию это `LogContext.ROOT`. Его можно использовать для настройки всех остальных логгеров:

```swift
// теперь все логгеры, которые не имеют явно указанного logLevel
// будут иметь logLevel = .ERROR
LogContext.ROOT.logLevel = .ERROR
```

Для логгирования структур, перечислений и классов можно использовать протокол `Loggable`, который уже имеет вычисляемое поле `log`, а так же его статический аналог:

```swift
class MyClass: Loggalbe {

  static func doSomethingStatic () {
    MyClass.log.info("hello")
  }

  func doSomething () {
    log.info("method invoked")
  }
}
```

<a name="sm02"></a>

### sm02

Этот модуль является *симулятором* сервера, для которого и пишется данный **iOS**-клиент.

Тест, который показывает сценарий использования `NetworkManager` и *симулятора* `SM-02` находится [здесь](./Tests/sm02/IntegrationTests.swift).

<a name="utils"></a>

### utils

В данном пакете находятся различные вспомогательные инструменты, помогающий в разработке.

#### Scheduler

Данный сервис можно использовать для планирования периодических задач и их отмены.

```swift
import utils


// запланировать периодически выполняемую задачу
let taskId = Scheduler.shared.schedule(every: .seconds(2), run: {
  print("Hello from periodic task")
})

// отменить задачу
Scheduler.shared.cancel(id: taskId)
```

#### Atomic

Эта обёртка используется для работы с атомарными объектами из разных потоков выполнения.

```swift
import utils


// создание атомарного объекта
let atomicValue = Atomic("hello")

// атомарная установка нового значения
atomicValue.store("hello world")

// синхронизированное чтение
let nonAtomicValue = atomicValue.load()

// асинхронная работа со значением
atomicValue.loading { value in
  value = "popa"
}
```

Класс находится [тут](./Sources/utils/threads/Atomic.swift).

#### ThreadedArray

Данный класс является потокобезопасной реализацией динамичного списка. Подробности [тут](./Sources/utils/threads/ThreadedArray.swift).

#### ThreadedDictionary

Данный класс является потокобезопасной реализацией словаря. Подробности [тут](./Sources/utils/threads/ThreadedDictionary.swift).

#### ThreadedQueue

Данный класс является потокобезопасной реализацией очереди. Подробности [тут](./Sources/utils/threads/ThreadedQueue.swift).

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
