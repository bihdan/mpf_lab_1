package sumdu.edu.ua.web;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {
    // Всі методи registrationBean видалено, оскільки DispatcherServlet
    // самостійно направляє запити до контролерів на основі анотацій @RequestMapping.
}