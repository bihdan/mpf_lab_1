package sumdu.edu.ua.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sumdu.edu.ua.config.Beans;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import java.io.IOException;

public class BooksApiServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(BooksApiServlet.class);
    private final CatalogRepositoryPort bookRepo = Beans.getBookRepo();
    private final ObjectMapper om = new ObjectMapper(); // Потрібно для JSON

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);
        String q = req.getParameter("q");
        String sort = req.getParameter("sort");

        try {
            var result = bookRepo.search(q, new PageRequest(page, size, sort));
            om.writeValue(resp.getWriter(), result); // Відправляємо результат клієнту
        } catch (Exception e) {
            log.error("API GET Error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        try {
            Book book = om.readValue(req.getInputStream(), Book.class);

            if (book.getTitle() == null || book.getTitle().isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Title required");
                return;
            }

            Book saved = bookRepo.add(book.getTitle(), book.getAuthor(), book.getPubYear());

            log.info("Book added: {} by {}", saved.getTitle(), saved.getAuthor());
            resp.setStatus(HttpServletResponse.SC_CREATED);
            om.writeValue(resp.getWriter(), saved);

        } catch (Exception e) {
            log.error("API POST Error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private int parseInt(String s, int def) {
        try { return (s != null) ? Integer.parseInt(s) : def; }
        catch (NumberFormatException e) { return def; }
    }
}