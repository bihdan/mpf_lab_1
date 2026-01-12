package sumdu.edu.ua.core.port;

import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import java.time.LocalDateTime;

public interface CommentRepositoryPort {
    void add(long bookId, String username, String text);
    Page<Comment> list(long bookId, String author, LocalDateTime since, PageRequest request);
    void delete(long bookId, long commentId, LocalDateTime createdAt);
    java.util.List<Comment> findByAuthor(long userId);
}