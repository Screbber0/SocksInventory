# Socks Inventory API

## Описание

Socks Inventory API — это Spring Boot приложение для управления складом носков. Поддерживаются операции по учету прихода, отпуска, фильтрации, обновления информации и загрузки данных из файлов.

## Требования

- **Java**: 17 или выше
- **Gradle**: 7.6 или выше
- **PostgreSQL**: Запущен на `http://localhost:5432`

## Установка

1. **Настройка конфигурации**

   Обновите файл `application.properties` в соответствии с вашей базой данных:

    ```properties
    # Настройки базы данных PostgreSQL
    spring.datasource.url=jdbc:postgresql://localhost:5432/socks_inventory
    spring.datasource.username=<ваш_логин>
    spring.datasource.password=<ваш_пароль>
    spring.jpa.hibernate.ddl-auto=update
    ```
   
   Задать уровень логирования можно через настройку
   ```properties
    # Настройка уровня логирования в приложении
    logging.level.root=INFO
    ```
    
2. **Сборка проекта**

   Выполните следующую команду для сборки:

    ```bash
    ./gradlew clean build
    ```

3. **Запуск приложения**

   После успешной сборки запустите приложение:

    ```bash
    ./gradlew bootRun
    ```

## Использование

После запуска приложение будет доступно по адресу `http://localhost:8080`. Swagger UI можно открыть по адресу:
`http://localhost:8080/swagger-ui/index.html`

## Тестирование

Для запуска тестов выполните следующую команду:

```bash
./gradlew test