package adapter;

import org.springframework.stereotype.Component;
import sumdu.edu.ua.core.domain.User;
import sumdu.edu.ua.persistence.repo.UserJpaRepository;

@Component
public class JpaUserRepositoryAdapter {
    private final UserJpaRepository repository;

    public JpaUserRepositoryAdapter(UserJpaRepository repository) {
        this.repository = repository;
    }

    public User findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
}