package sumdu.edu.ua.web;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.exceptions.CommentTooOldException;
import sumdu.edu.ua.core.exceptions.CommentValidationException;
import sumdu.edu.ua.core.exceptions.InvalidCommentDeleteException;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.service.CommentService;

import java.time.Instant;
import java.util.List;

@Controller
@RequestMapping("/comments")
public class CommentsController {

    private final CommentRepositoryPort commentRepo;
    private final CatalogRepositoryPort bookRepo;
    private final CommentService commentService;

    public CommentsController(CommentRepositoryPort commentRepo,
                              CatalogRepositoryPort bookRepo,
                              CommentService commentService) {
        this.commentRepo = commentRepo;
        this.bookRepo = bookRepo;
        this.commentService = commentService;
    }

    @GetMapping
    public String list(@RequestParam("bookId") long bookId, Model model) {
        Book book = bookRepo.findById(bookId);

        if (book == null) {
            return "redirect:/books";
        }

        List<Comment> comments = commentRepo
                .list(bookId, null, null, new PageRequest(0, 100, "id"))
                .getItems();

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);

        return "book-comments";
    }

    @PostMapping
    public String add(@RequestParam("bookId") long bookId,
                      @RequestParam("author") String author,
                      @RequestParam("text") String text,
                      RedirectAttributes redirectAttributes) {
        try {
            commentService.add(bookId, author, text);
        } catch (CommentValidationException | InvalidCommentDeleteException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/comments?bookId=" + bookId;
    }

    @PostMapping("/delete")
    public String delete(
            @RequestParam("bookId") long bookId,
            @RequestParam("commentId") long commentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdAt,
            RedirectAttributes redirectAttributes) {
        try {
            commentService.delete(bookId, commentId, createdAt);
        } catch (CommentTooOldException | InvalidCommentDeleteException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/comments?bookId=" + bookId;
    }
}