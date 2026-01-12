# Лабораторна робота №3
**Тема:** Spring Core & Boot. Принципи IoC та DI.

**Технології:** Java 25, Spring Boot 3.3.4, Jakarta Servlet API, Maven, H2 Database.

---

## 1. Мета роботи
Ознайомитися з принципами інверсії керування (IoC) та ін’єкції залежностей (DI) у Spring. Навчитися створювати біни, керувати їхнім життєвим циклом та застосовувати автоконфігурацію Spring Boot. Налаштувати застосунок за допомогою файлів конфігурації.

---

## 2. Відповіді на контрольні запитання

### 1. Що означає принцип інверсії керування (IoC) у Spring Boot і як він реалізований у цьому проєкті?
IoC означає передачу контролю над створенням та управлінням об'єктами від розробника до фреймворку. У цьому проєкті ми видалили клас-контейнер Beans. Тепер Spring сам знаходить класи, позначені як `@Repository`, `@Service` або `@Component`, створює їхні екземпляри та пов'язує їх між собою.

### 2. Яку роль виконує клас AppInit з анотацією `@SpringBootApplication`?
Це точка входу в застосунок. Анотація поєднує в собі:
- `@Configuration`: дозволяє визначати біни.
- `@EnableAutoConfiguration`: автоматично налаштовує інфраструктуру (Tomcat, БД, JSON-парсер).
- `@ComponentScan`: ініціює пошук бінів у вказаних пакетах.

### 3. Як у Spring Boot реєструються власні сервлети без використання web.xml?
Замість старого `web.xml` ми використовуємо програмну реєстрацію через бін `ServletRegistrationBean` у класі конфігурації, позначеному анотацією `@Configuration`.

### 4. Для чого використовується файл `application.properties` у цьому проєкті?
Він дозволяє змінювати параметри роботи програми без перекомпіляції коду. Ми використали його для зміни порту сервера, назви додатка та передачі власного повідомлення (welcome message).

### 5. Чому у Spring Boot не потрібно налаштовувати Jetty чи Tomcat вручну?
Spring Boot використовує вбудовані (embedded) сервери. Веб-сервер запускається як звичайна частина Java-процесу при старті методу `main`.

### 6. Що робить параметр `scanBasePackages = "sumdu.edu.ua"` у анотації `@SpringBootApplication`?
Оскільки проєкт багатомодульний, цей параметр вказує Spring шукати біни в усіх підпакетах `sumdu.edu.ua`, включаючи модулі `persistence` та `core`.

---

## 3. Демонстрація реалізації завдань

### 3.1. Реалізація бінів та DI (Конструктор та Поле)
У класі `ServletConfig` продемонстровано відразу кілька типів ін’єкції залежностей:

```java
@Configuration
public class ServletConfig {

    // 1. Демонстрація ін’єкції через ПОЛЕ (@Autowired)
    @Autowired
    private ObjectMapper objectMapper;

    // 2. Демонстрація отримання власного параметра (@Value)
    @Value("${app.custom.message}")
    private String welcomeMsg;

    @Bean // Додавання кастомного біна
    public ServletRegistrationBean<BooksServlet> booksServlet(CatalogRepositoryPort bookRepo) {
        // 3. Демонстрація ін’єкції через КОНСТРУКТОР (передача bookRepo)
        return new ServletRegistrationBean<>(new BooksServlet(bookRepo, welcomeMsg), "/books/*");
    }
}
```

### 3.2 Керування життєвим циклом біна

Клас `DbInit` демонструє виконання логіки одразу після створення біна:

```java
@Component
public class DbInit {

    @PostConstruct
    public void init() {
        System.out.println("!!! База даних ініціалізована біном DbInit !!!");
    }
}
```

### 3.3 Налаштування через application.properties
```properties
server.port=8081
spring.application.name=Book-Catalog-Lab3
app.custom.message=Вітаємо у нашій бібліотеці!
```

## 4. Результати роботи (Скріншоти)

### 4.1 Запуск застосунку та ініціалізація БД
На скріншоті відображено запуск Spring Boot-застосунку та виконання методу @PostConstruct.

<img width="1782" height="812" alt="image" src="https://github.com/user-attachments/assets/bd2ba790-dc0e-41a5-ac6a-67dde748283b" />


### 4.2 Робота пагінації та сортування
Демонстрація підрахунку сторінок та зміни порядку книг за вибраним полем.

<img width="767" height="662" alt="image" src="https://github.com/user-attachments/assets/a51df85e-ce63-426f-9b0c-9d5375b7abce" />


### 4.3 Використання власного параметра
Відображення повідомлення «Welcome to our library!», зчитаного з application.properties.

<img width="348" height="138" alt="image" src="https://github.com/user-attachments/assets/1e2ee999-5277-4b56-a86e-9e3ca129b6b1" />

### Інший функціонал, розроблений у ЛР2, залишився без змін.
