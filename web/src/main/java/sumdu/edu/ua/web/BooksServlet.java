package sumdu.edu.ua.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

import java.io.IOException;
import java.util.List;

@WebServlet("/books/*")
public class BooksServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(BooksServlet.class);
    private final CatalogRepositoryPort bookRepo;
    private final String welcomeMessage; // Поле для власного параметра

    // Оновлений конструктор для демонстрації DI
    public BooksServlet(CatalogRepositoryPort bookRepo, String welcomeMessage) {
        this.bookRepo = bookRepo;
        this.welcomeMessage = welcomeMessage;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String query = req.getParameter("q");
            String pageStr = req.getParameter("page");
            String sort = req.getParameter("sort");

            int page = (pageStr != null && !pageStr.isBlank()) ? Integer.parseInt(pageStr) : 0;

            String sortField = (sort != null && !sort.isBlank()) ? sort : "id";

            PageRequest pageRequest = new PageRequest(page, 5, sortField);

            var bookPage = bookRepo.search(query, pageRequest);

            req.setAttribute("bookPage", bookPage);
            req.setAttribute("books", bookPage.getItems());
            req.setAttribute("welcomeMessage", welcomeMessage);

            req.getRequestDispatcher("/WEB-INF/views/books.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Pagination error: {}", e.getMessage());
            resp.sendError(500);
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
            log.info("Book added successfully: {}", title);
            resp.sendRedirect(req.getContextPath() + "/books");
        } catch (Exception e) {
            log.error("Error adding book: {}", e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid data");
        }
    }
}