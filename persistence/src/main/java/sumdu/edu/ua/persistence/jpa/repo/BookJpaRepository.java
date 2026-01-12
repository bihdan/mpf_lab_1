package repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sumdu.edu.ua.core.domain.Book;

public interface BookJpaRepository extends JpaRepository<Book, Long> {
}