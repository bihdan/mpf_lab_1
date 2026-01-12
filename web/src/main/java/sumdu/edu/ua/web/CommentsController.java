package sumdu.edu.ua.web;

import io.javalin.Javalin;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.service.CommentService;

import java.util.List;
import java.util.Map;

public class CommentsController {

    private final CommentRepositoryPort commentRepo;
    private final CatalogRepositoryPort bookRepo;
    private final CommentService commentService;

    public CommentsController(CommentRepositoryPort commentRepo, CatalogRepositoryPort bookRepo) {
        this.commentRepo = commentRepo;
        this.bookRepo = bookRepo;
        this.commentService = new CommentService(commentRepo);
    }

    public void registerRoutes(Javalin app) {

        // GET /comments — перегляд коментарів до книги
        app.get("/comments", ctx -> {
            long bookId = ctx.queryParamAsClass("bookId", Long.class).get();

            Book book = bookRepo.findById(bookId);
            List<Comment> comments = commentRepo.list(bookId, null, null, new PageRequest(0, 20, "id")).getItems();

            ctx.render("book-comments.jsp", Map.of(
                    "book", book,
                    "comments", comments
            ));
        });

        // POST /comments — додавання нового коментаря (через форму)
        app.post("/comments", ctx -> {
            long bookId = ctx.formParamAsClass("bookId", Long.class).get();
            String author = ctx.formParam("author");
            String text = ctx.formParam("text");

            commentService.add(bookId, author, text);

            ctx.redirect("/comments?bookId=" + bookId);
        });

        // POST /comments/delete — видалення коментаря
        app.post("/comments/delete", ctx -> {
            long bookId = ctx.formParamAsClass("bookId", Long.class).get();
            long commentId = ctx.formParamAsClass("commentId", Long.class).get();
            Comment comment = commentRepo.list(bookId, null, null, new PageRequest(0, 100, "id"))
                    .getItems().stream()
                    .filter(c -> c.getId() == commentId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Коментар не знайдено"));

            commentService.delete(bookId, commentId, comment.getCreatedAt());

            ctx.redirect("/comments?bookId=" + bookId);
        });
    }
}