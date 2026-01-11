# Guestbook Web Application (Jakarta EE)

Базовий веб-додаток на базі **Jakarta EE**, що реалізує функціонал збереження та відображення відгуків користувачів.  
Проєкт демонструє роботу з **Maven**, **Servlets**, **JDBC** та вбудованою базою даних **H2**.

---

## Технічний стек та версії

- **Java SDK:** 19 (або 21)  
- **Maven:** 3.9+  
- **Jakarta Servlet API:** 5.0.0 (Jetty 11)  
- **Database:** H2 (Embedded)  
- **JSON library:** Jackson Databind 2.17.2  
- **Logging:** SLF4J 2.0.13 + Logback 1.5.13  

---

## Запуск додатка

Для збірки та запуску додатка через вбудований сервер Jetty використовуйте команду:

```bash
mvn jetty:run
```

Після запуску додаток буде доступний за адресою:
http://localhost:8080/

---

## База даних

**H2 JDBC URL:** `jdbc:h2:file:./data/guest;AUTO_SERVER=TRUE`

**Шлях до файлів:** Файли бази даних автоматично створюються в папці `./data/` у корені проєкту.

**Структура таблиці:** Таблиця comments містить поля `id (identity)`, `author (varchar 64)`, `text (varchar 1000)` та `created_at (timestamp)`. Створюється автоматично при першому старті програми.

---

## Список ендпоїнтів

| Метод      | Шлях         | Опис                     |
|------------|--------------|--------------------------|
| GET        | /            | Унікальний ідентифікатор |
| GET        | /comments    | Ім'я автора              |
| POST       | /comments    | Текст коментаря          |

---

## Валідація та коди відповідей:
- `author`: обов'язково, довжина до 64 символів.
- `text`: обов'язково, довжина до 1000 символів.
- **204 No Content:** Успішне додавання.
- **400 Bad Request:** Помилка валідації полів.
- **500 Internal Server Error:** Збій при роботі з базою даних.

---

## Логування
У проєкті налаштовано логування подій на рівні INFO. Після кожного успішного додавання відгуку в консоль сервера виводиться повідомлення:
`New comment added - ID: [id], Author: [name], Text length: [length]`

---

## Результати виконання:


<img width="1684" height="721" alt="Screenshot_10" src="https://github.com/user-attachments/assets/01b4fd7c-ae99-4fd0-81c5-94973566cc3c" />

<img width="1919" height="254" alt="Screenshot_11" src="https://github.com/user-attachments/assets/b681402e-860b-4143-8c8f-449a2d3c0060" />

<img width="1595" height="338" alt="Screenshot_12" src="https://github.com/user-attachments/assets/43c7731b-9ce7-45d3-a8d0-ebcd2feb1eba" />

