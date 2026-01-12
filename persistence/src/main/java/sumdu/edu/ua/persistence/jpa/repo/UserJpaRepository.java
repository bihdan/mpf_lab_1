package repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sumdu.edu.ua.core.domain.User;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}