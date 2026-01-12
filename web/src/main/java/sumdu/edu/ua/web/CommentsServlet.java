package sumdu.edu.ua.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import java.io.IOException;
import java.util.List;

public class CommentsServlet extends HttpServlet {
    private final CatalogRepositoryPort bookRepo;
    private final CommentRepositoryPort commentRepo;

    public CommentsServlet(CatalogRepositoryPort bookRepo, CommentRepositoryPort commentRepo) {
        this.bookRepo = bookRepo;
        this.commentRepo = commentRepo;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String bookIdStr = req.getParameter("bookId");
        if (bookIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/books");
            return;
        }
        long bookId = Long.parseLong(bookIdStr);
        try {
            Book book = bookRepo.findById(bookId);
            PageRequest pageRequest = new PageRequest(0, 20, "id");
            List<Comment> comments = commentRepo.list(bookId, null, null, pageRequest).getItems();

            req.setAttribute("book", book);
            req.setAttribute("comments", comments);
            req.getRequestDispatcher("/WEB-INF/views/book-comments.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Cannot load book details", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        String method = req.getParameter("_method");

        if ("delete".equalsIgnoreCase(method)) {
            long bookId = Long.parseLong(req.getParameter("bookId"));
            long commentId = Long.parseLong(req.getParameter("commentId"));
            commentRepo.delete(bookId, commentId);
            resp.sendRedirect(req.getContextPath() + "/comments?bookId=" + bookId);
            return;
        }

        long bookId = Long.parseLong(req.getParameter("bookId"));
        commentRepo.add(bookId, req.getParameter("author"), req.getParameter("text"));
        resp.sendRedirect(req.getContextPath() + "/comments?bookId=" + bookId);
    }
}