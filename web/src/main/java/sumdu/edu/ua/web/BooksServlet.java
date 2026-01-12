package sumdu.edu.ua.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sumdu.edu.ua.config.Beans;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

import java.io.IOException;

public class BooksServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(BooksServlet.class);
    private final CatalogRepositoryPort bookRepo = Beans.getBookRepo();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String query = req.getParameter("q");
            String pageStr = req.getParameter("page");
            String sort = req.getParameter("sort");

            int page = (pageStr != null && !pageStr.isBlank()) ? Integer.parseInt(pageStr) : 0;
            PageRequest pageRequest = new PageRequest(page, 5, (sort != null) ? sort : "id");

            var bookPage = bookRepo.search(query, pageRequest);

            req.setAttribute("bookPage", bookPage);
            req.setAttribute("books", bookPage.getItems());
            req.getRequestDispatcher("/WEB-INF/views/books.jsp").forward(req, resp);
        } catch (Exception e) {
            log.error("Error 500: Cannot load books", e);
            sendError(resp, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String title = req.getParameter("title");
            String author = req.getParameter("author");
            int pubYear = Integer.parseInt(req.getParameter("pubYear"));

            bookRepo.add(title, author, pubYear);
            log.info("INFO: Book added - {} by {}", title, author);
            resp.sendRedirect(req.getContextPath() + "/books");
        } catch (IllegalStateException e) {
            log.warn("WARN: Conflict while adding book - {}", e.getMessage());
            sendError(resp, 409, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR: Server error while adding book", e);
            sendError(resp, 500, "Server Error");
        }
    }

    private void sendError(HttpServletResponse resp, int status, String msg) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(String.format("{\"error\": \"%s\", \"status\": %d}", msg, status));
    }
}