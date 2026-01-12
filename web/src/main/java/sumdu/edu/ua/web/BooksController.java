package sumdu.edu.ua.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

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
    public String getAllBooks(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        var booksPage = bookRepo.search(q, new PageRequest(page, 20, sortBy));

        model.addAttribute("books", booksPage.getItems());
        model.addAttribute("query", q != null ? q : "");
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("total", booksPage.getTotal());
        model.addAttribute("currentPage", page);

        return "books";
    }

    // Решта методів (findById, showAddForm, addBook) залишаються без змін
    @GetMapping("/{id}")
    public String getBookDetails(@PathVariable("id") Long id, Model model) {
        Book book = bookRepo.findById(id);
        if (book == null) {
            model.addAttribute("message", "Книгу не знайдено");
            return "error";
        }
        var comments = commentRepo.list(id, null, null, new PageRequest(0, 100, "id")).getItems();
        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        return "book-details";
    }

}