package sumdu.edu.ua.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

@Configuration
public class ServletConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.custom.message:Default Welcome}")
    private String welcomeMessage;

    @Bean
    public ServletRegistrationBean<BooksServlet> booksServlet(CatalogRepositoryPort bookRepo) {
        return new ServletRegistrationBean<>(new BooksServlet(bookRepo, welcomeMessage), "/books/*");
    }

    @Bean
    public ServletRegistrationBean<CommentsServlet> commentsServlet(
            CatalogRepositoryPort bookRepo,
            CommentRepositoryPort commentRepo) {
        return new ServletRegistrationBean<>(new CommentsServlet(bookRepo, commentRepo), "/comments/*");
    }

    @Bean
    public ServletRegistrationBean<BooksApiServlet> booksApiServlet(CatalogRepositoryPort bookRepo) {
        return new ServletRegistrationBean<>(new BooksApiServlet(bookRepo, objectMapper), "/api/books/*");
    }
}