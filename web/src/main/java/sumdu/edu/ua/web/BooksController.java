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

        // Використовуємо PageRequest для пошуку та сортування
        var booksPage = bookRepo.search(q, new PageRequest(page, 20, sortBy));

        // Передаємо дані та стан фільтрів назад у шаблон
        model.addAttribute("books", booksPage.getItems());
        model.addAttribute("query", q != null ? q : "");
        model.addAttribute("sortBy", sortBy); // Щоб зберегти вибір у <select>
        model.addAttribute("total", booksPage.getTotal()); // Для пагінації
        model.addAttribute("currentPage", page); // Для підсвічування сторінки

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

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("bookForm", new Book());
        return "book-add";
    }

    @PostMapping
    public String addBook(@ModelAttribute("bookForm") Book book) {
        bookRepo.add(book.getTitle(), book.getAuthor(), book.getPubYear());
        return "redirect:/books";
    }
}