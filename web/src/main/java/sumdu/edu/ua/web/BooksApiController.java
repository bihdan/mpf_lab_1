package sumdu.edu.ua.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.domain.Comment;

import java.util.Map;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('ADMIN')") // Весь API доступний тільки для ADMIN
public class BooksApiController {
    private static final Logger log = LoggerFactory.getLogger(BooksApiController.class);
    private final CatalogRepositoryPort bookRepo;
    private final CommentRepositoryPort commentRepo;

    public BooksApiController(CatalogRepositoryPort bookRepo, CommentRepositoryPort commentRepo) {
        this.bookRepo = bookRepo;
        this.commentRepo = commentRepo;
    }

    @GetMapping("/books")
    public Page<Book> searchBooks(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        return bookRepo.search(q, new PageRequest(page, size, sortBy));
    }

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

    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("id") Long id) {
        try {
            var allComments = commentRepo.list(0, null, null, new PageRequest(0, 1000, "id")).getItems();
            Comment comment = allComments.stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Comment not found"));

            commentRepo.delete(comment.getBook().getId(), id, comment.getCreatedAt());
        } catch (Exception e) {
            log.error("Помилка при видаленні коментаря", e);
            throw e;
        }
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public Book createBook(@RequestBody Book book) {
        return bookRepo.save(book);
    }
}