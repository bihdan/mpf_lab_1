package sumdu.edu.ua.infrastructure;

import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.persistence.jdbc.DbInit;
import sumdu.edu.ua.persistence.jdbc.JdbcBookRepository;
import sumdu.edu.ua.persistence.jdbc.JdbcCommentRepository;

public class ApplicationInitializer {

    static {
        DbInit.init();
    }

    public static CatalogRepositoryPort createCatalogRepository() {
        return new JdbcBookRepository();
    }

    public static CommentRepositoryPort createCommentRepository() {
        return new JdbcCommentRepository();
    }
}