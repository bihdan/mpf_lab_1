package sumdu.edu.ua.web;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.util.Map;

public class BooksController {

    private final CatalogRepositoryPort bookRepo;
    private final CommentRepositoryPort commentRepo;

    public BooksController(CatalogRepositoryPort bookRepo, CommentRepositoryPort commentRepo) {
        this.bookRepo = bookRepo;
        this.commentRepo = commentRepo;
    }

    public void registerRoutes(Javalin app) {

        // GET /books — перегляд списку книг на сторінці
        app.get("/books", ctx -> {
            String q = ctx.queryParam("q");
            String sortBy = ctx.queryParamAsClass("sortBy", String.class).getOrDefault("id");
            int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);

            var booksPage = bookRepo.search(q, new PageRequest(page, 20, sortBy));

            ctx.render("books.jsp", Map.of(
                    "books", booksPage.getItems(),
                    "query", q != null ? q : ""
            ));
        });

        // GET /books/{id} — детальна сторінка книги
        app.get("/books/{id}", ctx -> {
            long id = ctx.pathParamAsClass("id", Long.class).get();

            Book book = bookRepo.findById(id);
            if (book == null) {
                ctx.status(HttpStatus.NOT_FOUND);
                ctx.render("error.jsp", Map.of("message", "Книгу не знайдено"));
                return;
            }

            var comments = commentRepo.list(id, null, null, new PageRequest(0, 100, "id")).getItems();

            ctx.render("book-details.jsp", Map.of(
                    "book", book,
                    "comments", comments
            ));
        });
    }
}