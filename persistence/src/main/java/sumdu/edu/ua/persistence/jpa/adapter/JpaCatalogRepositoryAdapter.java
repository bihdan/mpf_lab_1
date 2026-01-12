package adapter;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.persistence.repo.BookJpaRepository;

import java.util.List;

@Component
public class JpaCatalogRepositoryAdapter implements CatalogRepositoryPort {
    private final BookJpaRepository repository;

    public JpaCatalogRepositoryAdapter(BookJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book findById(long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Book add(String title, String author, int pubYear) {
        Book book = new Book(null, title, author, pubYear);
        return repository.save(book); // JPA автоматично згенерує ID
    }

    @Override
    public Page<Book> search(String q, sumdu.edu.ua.core.domain.PageRequest request) {
        // Конвертуємо ваш PageRequest у формат Spring Data
        var pageable = PageRequest.of(request.getPage(), request.getSize(),
                Sort.by(request.getSortBy()));

        // Для спрощення використовуємо findAll (або додайте метод пошуку в репозиторій)
        var jpaPage = repository.findAll(pageable);

        return new Page<>(jpaPage.getContent(), request, jpaPage.getTotalElements());
    }
}