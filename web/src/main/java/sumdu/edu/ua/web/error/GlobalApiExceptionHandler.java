package sumdu.edu.ua.web.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sumdu.edu.ua.core.exceptions.BookNotFoundException;
import sumdu.edu.ua.core.exceptions.CommentTooOldException;
import sumdu.edu.ua.core.exceptions.CommentValidationException;
import sumdu.edu.ua.core.exceptions.InvalidCommentDeleteException;

import java.util.Map;

@RestControllerAdvice(annotations = RestController.class)
public class GlobalApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalApiExceptionHandler.class);

    @ExceptionHandler({InvalidCommentDeleteException.class, CommentValidationException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException ex) {
        log.info("API Bad Request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(CommentTooOldException.class)
    public ResponseEntity<Map<String, String>> handleTooOld(CommentTooOldException ex) {
        log.info("Comment too old: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFound(BookNotFoundException ex) {
        log.info("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }
}