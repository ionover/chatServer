# ChatApp

**Java 22 · Maven**

## Описание
Простое клиент-серверное чат-приложение. Одна «комната общения», к которой могут подключаться несколько участников.  
При подключении каждый выбирает своё имя, все участники получают уведомления о входе/выходе и каждое сообщение.

![Общая схема](bpmn.png)

## Конфигурация
Файл настроек в `src/main/resources/(client/server)/settings.txt`:
```properties
host=localhost       # для клиента
port=12345           # порт сервера
```
Логи по умолчанию пишутся в `src/main/resources/file.log`.  
Для тестов используется `src/test/resources/file.log`.

## Логирование
- **ServerLogger** и **ClientLogger** дописывают события в файл лога.

## Тесты
- Простые unit-тесты для `ConfigLoader` и `ClientLogger`/`ServerLogger`.
- Тесты используют собственные конфиги и файл лога в `src/test/resources`.
