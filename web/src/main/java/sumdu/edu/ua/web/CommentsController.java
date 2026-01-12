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

    public CommentsController(CommentRepositoryPort commentRepo, CatalogRepositoryPort bookRepo) {
        this.commentRepo = commentRepo;
        this.bookRepo = bookRepo;
        this.commentService = new CommentService(commentRepo);
    }

    // GET /comments — перегляд коментарів до книги
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

        return "book-comments"; // Відкриває templates/book-comments.html
    }

    // POST /comments — додавання нового коментаря
    @PostMapping
    public String add(@RequestParam("bookId") long bookId,
                      @RequestParam("author") String author,
                      @RequestParam("text") String text) {

        commentService.add(bookId, author.trim(), text.trim());

        return "redirect:/comments?bookId=" + bookId;
    }

    // POST /comments/delete — видалення коментаря
    @PostMapping("/delete")
    public String delete(@RequestParam("bookId") long bookId,
                         @RequestParam("commentId") long commentId) {

        Comment comment = commentRepo.list(bookId, null, null, new PageRequest(0, 100, "id"))
                .getItems().stream()
                .filter(c -> c.getId() == commentId)
                .findFirst()
                .orElse(null);

        if (comment != null) {
            commentService.delete(bookId, commentId, comment.getCreatedAt());
        }

        return "redirect:/comments?bookId=" + bookId;
    }
}