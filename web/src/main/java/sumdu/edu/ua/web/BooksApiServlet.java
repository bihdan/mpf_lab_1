package sumdu.edu.ua.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import java.io.IOException;

public class BooksApiServlet extends HttpServlet {
    private final CatalogRepositoryPort bookRepo;
    private final ObjectMapper om;

    public BooksApiServlet(CatalogRepositoryPort bookRepo, ObjectMapper om) {
        this.bookRepo = bookRepo;
        this.om = om;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        int page = parseInt(req.getParameter("page"), 0);
        int size = parseInt(req.getParameter("size"), 10);
        var result = bookRepo.search(req.getParameter("q"), new PageRequest(page, size, req.getParameter("sort")));
        om.writeValue(resp.getWriter(), result);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Book book = om.readValue(req.getInputStream(), Book.class);
        Book saved = bookRepo.add(book.getTitle(), book.getAuthor(), book.getPubYear());
        resp.setStatus(HttpServletResponse.SC_CREATED);
        om.writeValue(resp.getWriter(), saved);
    }

    private int parseInt(String s, int def) {
        try { return (s != null) ? Integer.parseInt(s) : def; } catch (Exception e) { return def; }
    }
}