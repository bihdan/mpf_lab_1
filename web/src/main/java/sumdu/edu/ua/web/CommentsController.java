package sumdu.edu.ua.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.service.CommentService;

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
    public String list(@RequestParam long bookId, Model model) {
        Book book = bookRepo.findById(bookId);
        List<Comment> comments = commentRepo.list(bookId, null, null, new PageRequest(0, 20, "id")).getItems();

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        return "book-comments";
    }

    @PostMapping
    public String add(@RequestParam long bookId,
                      @RequestParam(required = false) String author,
                      @RequestParam String text) {
        commentService.add(bookId, author, text);
        return "redirect:/comments?bookId=" + bookId;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam long bookId,
                         @RequestParam long commentId) {

        Comment comment = commentRepo.list(bookId, null, null, new PageRequest(0, 100, "id"))
                .getItems().stream()
                .filter(c -> c.getId() == commentId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Коментар не знайдено"));

        commentService.delete(bookId, commentId, comment.getCreatedAt());

        return "redirect:/comments?bookId=" + bookId;
    }
}