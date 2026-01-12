package sumdu.edu.ua.core.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sumdu.edu.ua.core.domain.User;
import sumdu.edu.ua.core.port.UserRepositoryPort;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Користувача з ID " + id + " не знайдено"));
    }

    public void register(String email, String password, String nickname) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Користувач з таким email вже існує");
        }

        User u = new User();
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password));
        u.setNickname(nickname);
        u.setRole("USER");
        u.setVerified(false);
        u.setVerificationToken(UUID.randomUUID().toString());

        userRepository.save(u);
    }

    public boolean verifyAccount(String token) {
        return userRepository.findByVerificationToken(token)
                .map(u -> {
                    u.setVerified(true);
                    u.setRole("USER");
                    u.setVerificationToken(null);
                    userRepository.save(u);
                    return true;
                })
                .orElse(false);
    }
}