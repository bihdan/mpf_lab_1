package sumdu.edu.ua.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.User;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.service.UserService;

import java.util.List;

@Controller
public class UserController {

    private final CommentRepositoryPort commentRepo;
    private final UserService userService;

    public UserController(CommentRepositoryPort commentRepo, UserService userService) {
        this.commentRepo = commentRepo;
        this.userService = userService;
    }

    @GetMapping("/users/{id}/comments")
    public String userComments(@PathVariable("id") Long id, Model model) {
        List<Comment> comments = commentRepo.findByAuthor(id);

        String displayName;
        try {
            User user = userService.findByIdOrThrow(id);
            displayName = (user.getNickname() != null && !user.getNickname().isBlank())
                    ? user.getNickname()
                    : user.getEmail();
        } catch (RuntimeException e) {
            displayName = "Користувач #" + id;
        }

        model.addAttribute("username", displayName);
        model.addAttribute("comments", comments);

        return "user-comments";
    }
}