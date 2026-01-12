package sumdu.edu.ua.config;

import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.service.CommentService;
import sumdu.edu.ua.persistence.jdbc.DbInit;
import sumdu.edu.ua.persistence.jdbc.JdbcBookRepository;
import sumdu.edu.ua.persistence.jdbc.JdbcCommentRepository;

public class Beans {
    private static CatalogRepositoryPort bookRepo;
    private static CommentRepositoryPort commentRepo;
    private static CommentService commentService;

    public static void init() {
        DbInit.init();

        bookRepo = new JdbcBookRepository();
        commentRepo = new JdbcCommentRepository();
        commentService = new CommentService(commentRepo);
    }

    public static CatalogRepositoryPort getBookRepo() { return bookRepo; }
    public static CommentRepositoryPort getCommentRepo() { return commentRepo; }
    public static CommentService getCommentService() { return commentService; }
}