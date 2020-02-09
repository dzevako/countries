# countries
Система из 2 приложений для хранения, просмотра и обновления информации о странах.
Приложение сделано как одно, но запускается с различными параметрами.
С помощью параметров задается порт, на котором будет запущено приложение и свойство, определяющее конфигурацию бинов.
Первое приложение запускается как клиент и предоставляет REST-API для получения
информации о странах и возможность обновить список стран. Без модификации БД.
Второе приложение запускается как приемник JMS сообщений на обновление списка стран.
Приложение не имеет REST-API. Может модифицировать БД
База данных - встроенная H2. Запускает ее первое запущенное приложение, второе к ней подключается (порядок запуска приложений значения не имеет)

После скачивания исходников проекта необходимо подтянуть все зависимости
и выполнить команду для сборки "толстого" джарника:

        mvn package spring-boot:repackage

Запуск приложений:
1. REST-API

        java -jar target/countries-0.0.1-SNAPSHOT.jar 1 --server.port=8081
2. Модификатор БД 

        java -jar target/countries-0.0.1-SNAPSHOT.jar 2 --server.port=8082

Приложения подключаются с дефолтными настройками к JMS-серверу ActiveMQ, который можно скачать тут: http://activemq.apache.org/components/classic/download/
И запустить из папки установки:

        bin/activemq start

После запуска всех компонентов можно проверить работу приложений с помощью REST-API

Краткая документация по REST-API:

1. Получение страны:

- по идентификатору в БД:   

        GET localhost/8081/countries/id=183
- по названию:

        GET localhost/8081/countries/name=Россия
- по коду:                  

        GET localhost/8081/countries/code=ru
        
     результат этих запросов будет выглядеть вот так:
     
        {
          "id":183,
          "name":"Россия",
          "code":"RU"
        }

2. Получение фильтрованного списка стран. Поиск вхождения значения выполняется только в названии страны.

        GET localhost/8081/countries/query=ос
        
   результат:
        
        [
          {
            "id":151,
            "name":"Нидерланды",
            "code":"NL"
          },
          {
            "id":152,
            "name":"Нидерландские Антилы",
            "code":"AN"
          },
          {
            "id":219,
            "name":"Тринидад и Тобаго",
            "code":"TT"
          }
        ]

3. Загрузка справочника со списком стран. Загрузить можно только файл в формате csv.

        POST localhost/8081/countries/update
        Headers: Content-Type: multipart/form-
        Body: file: Выбранный файл .csv


Примечания:

По ходу написания приложений от версионирования REST было решено отказаться, потому что
Последняя (текущая и единственная) версия REST-API удовлетворяет всем требованиям к обеим версиям, в том числе обновление справочника стран.

