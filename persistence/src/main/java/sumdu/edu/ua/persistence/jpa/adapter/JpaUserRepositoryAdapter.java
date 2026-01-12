package sumdu.edu.ua.persistence.jpa.adapter;

import org.springframework.stereotype.Repository;
import sumdu.edu.ua.core.domain.User;
import sumdu.edu.ua.core.port.UserRepositoryPort;
import sumdu.edu.ua.persistence.jpa.repo.UserJpaRepository;

import java.util.Optional;

@Repository
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository repo;

    public JpaUserRepositoryAdapter(UserJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<User> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public Optional<User> findByVerificationToken(String token) {
        return repo.findByVerificationToken(token);
    }

    @Override
    public User save(User user) {
        return repo.save(user);
    }
}