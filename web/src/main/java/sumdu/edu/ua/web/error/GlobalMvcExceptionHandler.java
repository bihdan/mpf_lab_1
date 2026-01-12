package sumdu.edu.ua.web.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sumdu.edu.ua.core.exceptions.CommentTooOldException;
import sumdu.edu.ua.core.exceptions.CommentValidationException;
import sumdu.edu.ua.core.exceptions.InvalidCommentDeleteException;

@ControllerAdvice(basePackages = "sumdu.edu.ua.web")
public class GlobalMvcExceptionHandler {

    @ExceptionHandler({
            CommentTooOldException.class,
            InvalidCommentDeleteException.class,
            CommentValidationException.class
    })
    public String handleBusinessErrors(RuntimeException ex,
                                       RedirectAttributes redirectAttributes,
                                       HttpServletRequest request) {

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/books");
    }

}