package sumdu.edu.ua.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.infrastructure.ApplicationInitializer;

import java.util.Map;

public class JavalinBookApp {

    private static final Logger log = LoggerFactory.getLogger(JavalinBookApp.class);

    public static void main(String[] args) {

        // 1. Ініціалізація через шар Infrastructure
        CatalogRepositoryPort bookRepo = ApplicationInitializer.createCatalogRepository();
        CommentRepositoryPort commentRepo = ApplicationInitializer.createCommentRepository();

        // 2. Налаштування ObjectMapper
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        var jsonMapper = new JavalinJackson(mapper);

        // 3. Створення та конфігурація додатка
        var app = Javalin.create(cfg -> {
            cfg.staticFiles.add("/public");
            cfg.jsonMapper(jsonMapper);
            cfg.plugins.enableDevLogging();
        });

        // 4. Middleware для логування
        app.before(ctx -> log.info(">>> Запит: {} {}", ctx.method(), ctx.path()));
        app.after(ctx -> log.info("<<< Статус: {}", ctx.status()));

        app.exception(Exception.class, (e, ctx) -> {
            log.error("Критична помилка", e);
            ctx.status(500).json(Map.of("error", "Internal Error", "message", e.getMessage()));
        });

        // 5. Реєстрація маршрутів
        // Передаємо ОБИДВА репозиторії
        new BooksApiController(bookRepo, commentRepo).registerRoutes(app);

        app.get("/", ctx -> ctx.redirect("/books.html"));
        app.get("/books", ctx -> ctx.redirect("/books.html"));

        // 6. Запуск сервера
        app.start(8080);
    }
}