package sumdu.edu.ua.web;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.util.Map;

public class BooksApiController {
    private static final Logger log = LoggerFactory.getLogger(BooksApiController.class);
    private final CatalogRepositoryPort bookRepo;
    private final CommentRepositoryPort commentRepo;

    public BooksApiController(CatalogRepositoryPort bookRepo, CommentRepositoryPort commentRepo) {
        this.bookRepo = bookRepo;
        this.commentRepo = commentRepo;
    }

    public void registerRoutes(Javalin app) {

        // GET /api/books — Пошук та сортування
        app.get("/api/books", ctx -> {
            try {
                String q = ctx.queryParam("q");
                String sortBy = ctx.queryParamAsClass("sortBy", String.class).getOrDefault("id");
                int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);
                int size = ctx.queryParamAsClass("size", Integer.class).getOrDefault(20);

                Page<Book> result = bookRepo.search(q, new PageRequest(page, size, sortBy));
                ctx.json(result);
            } catch (Exception e) {
                log.error("Помилка API при отриманні книг", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });

        // GET /api/books/{id} — Дані для сторінки коментарів
        app.get("/api/books/{id}", ctx -> {
            try {
                long id = ctx.pathParamAsClass("id", Long.class).get();
                Book book = bookRepo.findById(id);
                var commentsPage = commentRepo.list(id, null, null, new PageRequest(0, 100, "id"));
                ctx.json(Map.of("book", book, "comments", commentsPage.getItems()));
            } catch (Exception e) {
                ctx.status(HttpStatus.NOT_FOUND);
            }
        });

        // POST /api/comments — Додавання коментаря
        app.post("/api/comments", ctx -> {
            try {
                var body = ctx.bodyAsClass(Map.class);
                long bookId = Long.parseLong(body.get("bookId").toString());
                String author = (String) body.get("author");
                String text = (String) body.get("text");

                commentRepo.add(bookId, author, text);
                ctx.status(HttpStatus.CREATED);
            } catch (Exception e) {
                ctx.status(HttpStatus.BAD_REQUEST);
            }
        });

        // DELETE /api/comments/{id} — Видалення коментаря
        app.delete("/api/comments/{id}", ctx -> {
            try {
                long id = ctx.pathParamAsClass("id", Long.class).get();
                // Викликаємо метод delete з порту. bookId можна передати 0,
                // якщо логіка репозиторію дозволяє видалення тільки за id коментаря
                commentRepo.delete(0, id);
                ctx.status(HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                log.error("Помилка при видаленні коментаря", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });

        // POST /api/books — Додавання нової книги
        app.post("/api/books", ctx -> {
            try {
                Book book = ctx.bodyAsClass(Book.class);
                Book saved = bookRepo.add(book.getTitle(), book.getAuthor(), book.getPubYear());
                ctx.status(HttpStatus.CREATED).json(saved);
            } catch (Exception e) {
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }
}