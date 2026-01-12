package repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sumdu.edu.ua.core.domain.Comment;
import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
    // Пошук коментарів конкретного користувача
    List<Comment> findByUserId(Long userId);
}