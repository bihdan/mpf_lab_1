package sumdu.edu.ua.service;

import org.junit.jupiter.api.Test;
import sumdu.edu.ua.core.exceptions.CommentTooOldException;
import sumdu.edu.ua.core.exceptions.CommentValidationException;
import sumdu.edu.ua.core.exceptions.InvalidCommentDeleteException;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.service.CommentService;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private final CommentRepositoryPort repo = mock(CommentRepositoryPort.class);
    private final CommentService service = new CommentService(repo);

    @Test
    void delete_tooOld_throwsCommentTooOldException() {
        Instant oldDate = Instant.now().minusSeconds(48 * 3600);
        assertThrows(CommentTooOldException.class, () -> service.delete(1L, 1L, oldDate));
        verifyNoInteractions(repo);
    }

    @Test
    void delete_validData_callsRepositoryDelete() {
        Instant now = Instant.now().minusSeconds(3600);
        service.delete(1L, 1L, now);
        verify(repo).delete(1L, 1L);
    }

    @Test
    void delete_invalidId_throwsInvalidCommentDeleteException() {
        assertThrows(InvalidCommentDeleteException.class,
                () -> service.delete(-1L, 1L, Instant.now()));
        verifyNoInteractions(repo);
    }

    @Test
    void add_shortText_throwsCommentValidationException() {
        assertThrows(CommentValidationException.class,
                () -> service.add(1L, "Author", "Hi"));

        verifyNoInteractions(repo);
    }
}