package sumdu.edu.ua.persistence.jpa.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sumdu.edu.ua.core.domain.Book;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Book> search(@Param("q") String q, Pageable pageable);
}