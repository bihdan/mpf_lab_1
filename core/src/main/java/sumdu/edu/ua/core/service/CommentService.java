package sumdu.edu.ua.core.service;

import sumdu.edu.ua.core.port.CommentRepositoryPort;
import java.time.Duration;
import java.time.Instant;

public class CommentService {
    private final CommentRepositoryPort repo;

    public CommentService(CommentRepositoryPort repo) {
        this.repo = repo;
    }

    public void add(long bookId, String author, String text) {
        if (bookId <= 0) {
            throw new IllegalArgumentException("ID книги має бути позитивним числом");
        }

        if (text == null || text.trim().isBlank()) {
            throw new IllegalArgumentException("Текст відгуку не може бути порожнім");
        }

        String finalAuthor = (author == null || author.trim().isBlank()) ? "Anonymous" : author;

        repo.add(bookId, finalAuthor, text);
    }

    public void delete(long bookId, long commentId, Instant createdAt) {
        if (createdAt != null && Duration.between(createdAt, Instant.now()).toHours() > 24) {
            throw new IllegalStateException("Comment too old to delete (older than 24h)");
        }
        repo.delete(bookId, commentId);
    }
}