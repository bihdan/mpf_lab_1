package sumdu.edu.ua.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/books")
public class BooksController {

    private final CatalogRepositoryPort bookRepo;
    private final CommentRepositoryPort commentRepo;

    public BooksController(CatalogRepositoryPort bookRepo, CommentRepositoryPort commentRepo) {
        this.bookRepo = bookRepo;
        this.commentRepo = commentRepo;
    }

    @GetMapping
    @ResponseBody
    public List<Book> getAllBooks(@RequestParam(required = false) String q,
                                  @RequestParam(defaultValue = "id") String sortBy,
                                  @RequestParam(defaultValue = "0") int page) {
        PageRequest pageRequest = new PageRequest(page, 20, sortBy);
        return bookRepo.search(q, pageRequest).getItems();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Map<String, Object> getBook(@PathVariable long id) {
        Book book = bookRepo.findById(id);
        if (book == null) {
            throw new IllegalArgumentException("Книгу не знайдено");
        }

        var comments = commentRepo.list(id, null, null, new PageRequest(0, 100, "id")).getItems();

        return Map.of(
                "book", book,
                "comments", comments
        );
    }
}