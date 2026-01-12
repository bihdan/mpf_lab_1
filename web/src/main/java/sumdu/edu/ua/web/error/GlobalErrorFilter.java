package sumdu.edu.ua.web.error;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

@WebFilter("/*")
public class GlobalErrorFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(GlobalErrorFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.setContentType("application/json;charset=UTF-8");

            int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR; // 500 за замовчуванням
            String message = e.getMessage();

            if (e.getMessage().contains("already exists") || e.getCause() instanceof IllegalStateException) {
                status = HttpServletResponse.SC_CONFLICT;
                log.warn("Conflict detected: {}", message);
            } else {
                log.error("Unhandled error: {}", message, e);
            }

            resp.setStatus(status);
            resp.getWriter().write(String.format("{\"error\": \"%s\", \"status\": %d}", message, status));
        }
    }
}