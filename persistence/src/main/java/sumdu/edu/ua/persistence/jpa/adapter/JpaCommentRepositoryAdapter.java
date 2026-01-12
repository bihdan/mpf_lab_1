package sumdu.edu.ua.persistence.jpa.adapter;

import org.springframework.stereotype.Repository;
import sumdu.edu.ua.core.domain.*;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.persistence.jpa.repo.BookJpaRepository;
import sumdu.edu.ua.persistence.jpa.repo.CommentJpaRepository;
import sumdu.edu.ua.persistence.jpa.repo.UserJpaRepository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JpaCommentRepositoryAdapter implements CommentRepositoryPort {
    private final CommentJpaRepository repo;
    private final BookJpaRepository bookRepo;
    private final UserJpaRepository userRepo;
    public JpaCommentRepositoryAdapter(CommentJpaRepository repo,
                                       BookJpaRepository bookRepo,
                                       UserJpaRepository userRepo) {
        this.repo = repo;
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
    }

    @Override
    public void add(long bookId, String username, String text) {
        Comment comment = new Comment();

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Книгу не знайдено"));

        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Користувача з email '" + username + "' не знайдено"));

        comment.setBook(book);
        comment.setUser(user);
        comment.setText(text);
        comment.setCreatedAt(LocalDateTime.now());

        repo.save(comment);
    }

    @Override
    public Page<Comment> list(long bookId, String author, LocalDateTime since, PageRequest request) {
        List<Comment> all = repo.findByBookIdOrderByCreatedAtDesc(bookId);
        int from = request.getPage() * request.getSize();
        int to = Math.min(from + request.getSize(), all.size());
        List<Comment> content = from >= all.size() ? List.of() : all.subList(from, to);
        return new Page<>(content, request, (long) all.size());
    }

    @Override
    public void delete(long bookId, long commentId, LocalDateTime createdAt) {
        repo.deleteById(commentId);
    }

    @Override
    public List<Comment> findByAuthor(long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }
}