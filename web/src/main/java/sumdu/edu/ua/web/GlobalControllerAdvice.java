package sumdu.edu.ua.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("urlBuilder")
    public ServletUriComponentsBuilder urlBuilder(HttpServletRequest request) {
        return ServletUriComponentsBuilder.fromRequest(request);
    }
}