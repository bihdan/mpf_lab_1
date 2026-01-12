package sumdu.edu.ua.core.port;

import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;

public interface CatalogRepositoryPort {
    Book findById(Long id);
    Page<Book> search(String query, PageRequest pageRequest);
    Book save(Book book);
    void delete(Long id);
}