package sumdu.edu.ua.persistence.jpa.adapter;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.persistence.jpa.repo.BookJpaRepository;

@Repository
public class JpaCatalogRepositoryAdapter implements CatalogRepositoryPort {

    private final BookJpaRepository repo;

    public JpaCatalogRepositoryAdapter(BookJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Page<Book> search(String query, PageRequest request) {
        var pageable = org.springframework.data.domain.PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(request.getSortBy())
        );

        org.springframework.data.domain.Page<Book> page;
        if (query == null || query.isBlank()) {
            page = repo.findAll(pageable);
        } else {
            page = repo.search(query, pageable);
        }

        return new Page<>(
                page.getContent(),
                request,
                page.getTotalElements()
        );
    }

    @Override
    public Book findById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public Book save(Book book) {
        return repo.save(book);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}