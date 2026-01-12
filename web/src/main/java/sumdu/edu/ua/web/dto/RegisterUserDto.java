package sumdu.edu.ua.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterUserDto {

    @Email(message = "Некоректний email")
    @NotBlank(message = "Email обов'язковий")
    private String email;

    @Size(min = 6, message = "Пароль має бути не менше 6 символів")
    private String password;

    private String nickname;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}