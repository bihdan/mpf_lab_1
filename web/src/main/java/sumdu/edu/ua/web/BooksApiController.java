package sumdu.edu.ua.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class BooksApiController {
    private static final Logger log = LoggerFactory.getLogger(BooksApiController.class);
    private final CatalogRepositoryPort bookRepo;
    private final CommentRepositoryPort commentRepo;

    public BooksApiController(CatalogRepositoryPort bookRepo, CommentRepositoryPort commentRepo) {
        this.bookRepo = bookRepo;
        this.commentRepo = commentRepo;
    }

    // GET /api/books — Пошук та сортування книг
    @GetMapping("/books")
    public Page<Book> searchBooks(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        return bookRepo.search(q, new PageRequest(page, size, sortBy));
    }

    // GET /api/books/{id} — Дані для детальної інформації та коментарів
    @GetMapping("/books/{id}")
    public Map<String, Object> getBookWithComments(@PathVariable("id") Long id) {
        Book book = bookRepo.findById(id);
        if (book == null) {
            throw new RuntimeException("Book not found");
        }
        var commentsPage = commentRepo.list(id, null, null, new PageRequest(0, 100, "id"));

        return Map.of(
                "book", book,
                "comments", commentsPage.getItems()
        );
    }

    // POST /api/comments — Додавання нового коментаря
    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public void addComment(@RequestBody Map<String, Object> body) {
        try {
            long bookId = Long.parseLong(body.get("bookId").toString());
            String author = (String) body.get("author");
            String text = (String) body.get("text");

            commentRepo.add(bookId, author, text);
        } catch (Exception e) {
            log.error("Помилка при додаванні коментаря через API", e);
            throw e;
        }
    }

    // DELETE /api/comments/{id} — Видалення коментаря
    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Встановлює статус 204
    public void deleteComment(@PathVariable("id") Long id) {
        try {
            commentRepo.delete(0, id);
        } catch (Exception e) {
            log.error("Помилка при видаленні коментаря", e);
            throw e;
        }
    }

    // POST /api/books — Реєстрація нової книги
    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public Book createBook(@RequestBody Book book) {
        return bookRepo.add(book.getTitle(), book.getAuthor(), book.getPubYear());
    }
}