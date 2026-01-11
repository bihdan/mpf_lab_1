package sumdu.edu.ua.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CommentServlet extends HttpServlet {

    private final CommentDao dao = new CommentDao();
    private final ObjectMapper om = new ObjectMapper();
    private static final Logger log =
            LoggerFactory.getLogger(CommentServlet.class);

    /**
     * GET /comments
     * 200 + JSON
     * 500 — помилка БД
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json; charset=UTF-8");

        try {
            var comments = dao.list();
            om.writeValue(resp.getWriter(), comments);
        } catch (Exception ex) {
            log.error("Database error when fetching comments", ex);
            resp.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "DB error"
            );
        }
    }

    /**
     * POST /comments
     * 204 — успіх
     * 400 — валідація
     * 500 — помилка БД
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");

        String author = req.getParameter("author");
        String text = req.getParameter("text");

        // Валідація
        if (author == null || author.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "author is required");
            return;
        }
        if (text == null || text.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "text is required");
            return;
        }
        if (author.length() > 64) {
            resp.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "author must be ≤ 64 characters"
            );
            return;
        }
        if (text.length() > 1000) {
            resp.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "text must be ≤ 1000 characters"
            );
            return;
        }

        String trimmedAuthor = author.trim();
        String trimmedText = text.trim();

        try {
            long id = dao.add(trimmedAuthor, trimmedText);

            log.info(
                    "New comment added - ID: {}, Author: {}, Text length: {}",
                    id, trimmedAuthor, trimmedText.length()
            );

            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (Exception ex) {
            log.error("Database error when adding comment", ex);
            resp.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "DB error"
            );
        }
    }
}
