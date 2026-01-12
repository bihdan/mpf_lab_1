# Комплексна лабораторна робота

**Тема:** Розгортання багатомодульного Spring Boot-застосунку у хмарному середовищі Render

---

## 1. Структура проєкту

Проєкт реалізований як багатомодульна Maven-система:

- **core** — містить доменні моделі (Book, User), порти та сервіси бізнес-логіки (UserService, MailService).
- **persistence** — відповідає за роботу з БД, містить JPA-репозиторії та Flyway-міграції.
- **web** — містить Spring MVC контролери, Thymeleaf-шаблони, налаштування Spring Security та конфігурацію Docker.

---

## 2. Конфігурація для Production

Для запуску застосунку в хмарі параметри підключення до БД та зовнішніх сервісів винесені у змінні середовища (`application.properties`):

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.flyway.enabled=true
mail.resend.api-key=${RESEND_API_KEY}
server.port=${PORT:8080}
```

- `SPRING_DATASOURCE_URL` — URL підключення до бази даних
- `RESEND_API_KEY` — інтеграція з HTTP API Resend для розсилки листів
- `PORT` — динамічний порт для Render (за замовчуванням 8080)

---

## 3. Контейнеризація (Dockerfile)

Застосунок розгортається через Docker. Використовується офіційний образ JDK 21:

```dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/web-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

---

## 4. Скріншоти виконання роботи

### 4.1. Інфраструктура та налаштування

**Панель керування Render із налаштованими змінними середовища (БД, API ключі, порти):**

<img width="1606" height="874" alt="image_2026-01-07_14-17-42" src="https://github.com/user-attachments/assets/4c4d1934-057c-42e8-817f-ae8177df4ca1" />


**Статус сервісу та публічний URL:**

<img width="577" height="462" alt="image_2026-01-07_14-13-15 (2)" src="https://github.com/user-attachments/assets/df3dbb78-8ca5-45cd-a4a4-7abb2055673e" />


### 4.2. Логи запуску та роботи

**Успішний старт застосунку та логування відправки листів через Resend API:**

<img width="1590" height="879" alt="image_2026-01-07_14-16-40" src="https://github.com/user-attachments/assets/9b964251-1b67-40f7-8fd3-bc7d2be1c074" />


### 4.3. Веб-інтерфейс та функціонал

**Головна сторінка зі списком книг:**

<img width="1102" height="943" alt="image_2026-01-07_14-16-10 (2)" src="https://github.com/user-attachments/assets/39f5d12c-4ad3-478c-bb02-84d41ab20349" />


**Форма додавання нової книги:**

<img width="727" height="723" alt="image_2026-01-07_14-16-10" src="https://github.com/user-attachments/assets/ea3ff9f0-fa7f-4007-ad1b-42e7b87be210" />


### 4.4. Робота з базою даних та поштою

**Підтвердження реєстрації на пошті (Resend API):**

<img width="799" height="112" alt="image_2026-01-07_14-13-15 (4)" src="https://github.com/user-attachments/assets/d0765ea6-4e1f-4313-ab21-d5ff7a83202a" />
<img width="974" height="650" alt="image_2026-01-07_14-13-15 (3)" src="https://github.com/user-attachments/assets/db5ef88b-750f-4f84-a800-51494be4724e" />



**Статус відправки листів у панелі Resend:**

<img width="1443" height="415" alt="image_2026-01-07_14-17-12" src="https://github.com/user-attachments/assets/d5f3cd7e-4ce7-47cb-b735-f716bc74c059" />


**Перегляд даних користувачів у віддаленій БД через консоль IntelliJ IDEA:**

<img width="1920" height="1030" alt="image_2026-01-06_23-52-45" src="https://github.com/user-attachments/assets/510d6c95-6bfa-4786-a513-536ce1dea7bd" />


---

## 5. Відповіді на теоретичні питання

### Чому не деплой вручну, а репозиторій?

Це забезпечує принцип CI/CD (Continuous Integration/Deployment). Render автоматично реагує на кожен push у GitHub, клонує код, збирає його та оновлює сервіс. Це мінімізує людський фактор і дозволяє швидко оновлювати продукт.

### Роль Maven у розгортанні?

Maven автоматизує збірку проєкту: завантажує залежності, компілює код, запускає тести та пакує модулі в JAR-файл. Під час деплою виконуються етапи `clean`, `compile`, `test` та `package`.

### Для чого змінні середовища?

Вони дозволяють відокремити конфігурацію від коду. Не можна зберігати в коді:

- Паролі до БД
- API-ключі (як наш Resend Key)
- Секрети Spring Security

Це питання безпеки та гнучкості (один код — різні налаштування для тесту та продакшну).

### Docker vs Класичний сервер?

Docker гарантує відтворюваність: застосунок запускається в ізольованому оточенні з усіма залежностями. Це вирішує проблему "на моєму комп'ютері працювало, а на сервері — ні". Також це легше масштабувати.

---

## Висновки

Найскладнішим етапом було налаштування розсилки листів через HTTP API через обмеження портів на Render. Використання Docker дозволило швидко розгорнути застосунок без ручного налаштування середовища Java на сервері. Робота зі змінними середовища забезпечила безпеку конфіденційних даних.
