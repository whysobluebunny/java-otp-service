# OTP Service — Java Spring Boot

Этот проект реализует сервис генерации, отправки и проверки OTP-кодов (одноразовых паролей) с возможностью отправки по Email, Telegram, SMS и сохранения в файл. Основан на Spring Boot + PostgreSQL.

## Функционал

- Регистрация/аутентификация пользователей (JWT)
- Разграничение ролей: `ADMIN`, `USER`
- Генерация OTP-кодов с настройкой длины и времени жизни
- Отправка OTP через:
  - Email (JavaMail)
  - Telegram Bot API
  - SMPP (эмулятор: SMPPsim)
  - Сохранение в файл
- Проверка OTP-кода (валиден, просрочен, использован)
- Автоматическая деактивация просроченных кодов
- Админ-панель:
  - Изменение конфигурации OTP
  - Удаление пользователей

## Технологии

- Java 21
- Spring Boot 3.2+
- Spring Security (JWT)
- PostgreSQL 17
- Maven
- Lombok
- JavaMail
- OpenSMPP / SMPPsim
- Telegram Bot API (через HTTP)

## Структура

- `controller/` — REST-контроллеры
- `service/` — бизнес-логика
- `model/` — сущности JPA (User, OTP, Config...)
- `security/` — JWT токены и фильтры
- `scheduler/` — авто-деактивация OTP
- `notification/` — email, telegram, sms и file-рассылки
- `repository/` — JPA-интерфейсы

## Конфигурация

### `application.yml`
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/otpdb
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
jwt:
  secret: your_secure_secret_key_32_chars_min
  expiration: 3600000
```

### `email.properties` (в `src/main/resources`)
```properties
email.username=your_email@example.com
email.password=your_password
email.from=your_email@example.com
mail.smtp.host=smtp.example.com
mail.smtp.port=587
mail.smtp.auth=true
mail.smtp.starttls.enable=true
```

### `sms.properties`
```properties
smpp.host=localhost
smpp.port=2775
smpp.system_id=smppclient1
smpp.password=password
smpp.system_type=OTP
smpp.source_addr=OTPService
```

## Тестирование с Postman

### 0. Админские операции

#### Обновление конфигурации OTP
```
PUT /api/admin/otp-config
Authorization: Bearer <TOKEN>
```
```json
{
  "codeLength": 6,
  "expirationSeconds": 120
}
```

#### Получение списка пользователей
```
GET /api/admin/users
Authorization: Bearer <TOKEN>
```

#### Удаление пользователя
```
DELETE /api/admin/users/2
Authorization: Bearer <TOKEN>
```


### 1. Регистрация пользователя
```
POST /api/auth/register
```
```json
{
  "login": "user1",
  "password": "pass123",
  "role": "USER"
}
```

### 2. Логин (вернет токен)
```
POST /api/auth/login
```
```json
{
  "login": "user1",
  "password": "pass123"
}
```

### 3. Генерация и отправка OTP
```
POST /api/otp/send
Authorization: Bearer <TOKEN>
```
```json
{
  "operationId": "transfer_987",
  "channels": ["EMAIL", "FILE"],
  "email": "target@example.com"
}
```

### 4. Валидация OTP
```
POST /api/otp/validate
Authorization: Bearer <TOKEN>
```
```json
{
  "operationId": "transfer_987",
  "code": "123456"
}
```

## Запуск

```bash
mvn clean install
```
```bash
java -jar target/otp-service.jar
```

## Админ

Доступные админские операции:

- `PUT /api/admin/otp-config` — изменить длину и срок действия OTP-кодов
- `GET /api/admin/users` — получить список всех пользователей, кроме администраторов
- `DELETE /api/admin/users/{id}` — удалить пользователя и все связанные с ним OTP-коды
- Только один админ может быть зарегистрирован
- `@PreAuthorize` защищает `/api/admin/**`

## Планировщик
Автоматически помечает OTP-коды как `EXPIRED`, если их срок действия истек (`@Scheduled(fixedRate = 60000)`).

---
Вот отдельный блок `README` про логирование HTTP-запросов и ответов:

---

## Логирование

В проекте реализовано логирование HTTP-запросов и ответов с помощью `OncePerRequestFilter`.

### Что логируется:

- Метод запроса (`GET`, `POST` и т. д.)
- URI запроса
- Тело запроса (JSON)
- Статус ответа
- Тело ответа

### Пример вывода в лог:

```
REQUEST | POST /api/auth/login | body: {"login":"admin","password":"123456"}
RESPONSE | status: 200 | body: {"token":"eyJhbGciOiJIUzI1NiJ9..."}
```

### Реализация:

Используется обёртка `ContentCachingRequestWrapper` и `ContentCachingResponseWrapper`:

---

#### Разработка: Бондаренко А.М. @whysobluebunny

Для запуска проекта потребуется Java 17+, PostgreSQL 17 и любой эмулятор SMPP (например, SMPPsim).

