package sumdu.edu.ua.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.web.mail.MailService;

@Controller
@RequestMapping("/books")
public class BookFormController {

    private final CatalogRepositoryPort bookRepo;
    private final MailService mailService;

    public BookFormController(CatalogRepositoryPort bookRepo, MailService mailService) {
        this.bookRepo = bookRepo;
        this.mailService = mailService;
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddForm(Model model) {
        model.addAttribute("bookForm", new Book());
        return "book-add";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String addBook(@ModelAttribute("bookForm") Book book) {
        Book savedBook = bookRepo.save(book);

        try {
            mailService.sendNewBookEmail(savedBook);
        } catch (Exception e) {
            System.err.println("Email notification failed: " + e.getMessage());
        }

        return "redirect:/books";
    }
}