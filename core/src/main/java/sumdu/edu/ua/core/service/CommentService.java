package sumdu.edu.ua.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sumdu.edu.ua.core.exceptions.CommentTooOldException;
import sumdu.edu.ua.core.exceptions.CommentValidationException;
import sumdu.edu.ua.core.exceptions.InvalidCommentDeleteException;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.time.Duration;
import java.time.Instant;

public class CommentService {
    private static final Logger log = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepositoryPort repo;

    public CommentService(CommentRepositoryPort repo) {
        this.repo = repo;
    }

    public void add(long bookId, String author, String text) {
        if (bookId <= 0) {
            throw new InvalidCommentDeleteException("ID книги має бути позитивним числом");
        }
        if (text == null || text.trim().length() < 3) {
            throw new CommentValidationException("Текст коментаря занадто короткий (мінімум 3 символи)");
        }

        String finalAuthor = (author == null || author.trim().isBlank()) ? "Anonymous" : author;
        repo.add(bookId, finalAuthor, text);
    }

    public void delete(long bookId, long commentId, Instant createdAt) {
        if (bookId <= 0 || commentId <= 0) {
            log.warn("Invalid delete request: bookId={}, commentId={}", bookId, commentId);
            throw new InvalidCommentDeleteException("Некоректний ідентифікатор книги або коментаря");
        }

        if (createdAt == null) {
            log.warn("Delete request failed: createdAt is null for commentId={}", commentId);
            throw new InvalidCommentDeleteException("Дата створення коментаря обов'язкова");
        }

        long hoursPassed = Duration.between(createdAt, Instant.now()).toHours();
        if (hoursPassed >= 24) {
            log.info("Attempt to delete old comment: commentId={}, age={}h", commentId, hoursPassed);
            throw new CommentTooOldException("Коментар створено більше ніж 24 години тому і не може бути видалений");
        }

        repo.delete(bookId, commentId);
    }

    
}