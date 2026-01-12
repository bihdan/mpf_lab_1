package sumdu.edu.ua.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sumdu.edu.ua.core.service.UserService;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;

    public GlobalControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("urlBuilder")
    public ServletUriComponentsBuilder urlBuilder(HttpServletRequest request) {
        return ServletUriComponentsBuilder.fromRequest(request);
    }

    @ModelAttribute("currentUserNickname")
    public String currentUserNickname() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        try {
            return userService.findByEmailOrThrow(auth.getName()).getNickname();
        } catch (Exception e) {
            return null;
        }
    }
}