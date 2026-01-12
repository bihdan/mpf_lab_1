package sumdu.edu.ua.web;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.service.UserService;
import sumdu.edu.ua.web.dto.RegisterUserDto;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new RegisterUserDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(
            @ModelAttribute("user") @Valid RegisterUserDto dto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.register(dto.getEmail(), dto.getPassword(), dto.getNickname());
            return "redirect:/login?registered";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/confirm")
    public String confirm(@RequestParam("token") String token, Model model) {
        boolean ok = userService.verifyAccount(token);
        if (ok) {
            return "auth/confirm-success";
        } else {
            model.addAttribute("error", "Некоректний або застарілий токен");
            return "auth/confirm-error";
        }
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "403";
    }
}