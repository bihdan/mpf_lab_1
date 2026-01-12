package sumdu.edu.ua.persistence.jdbc;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

@Component
public final class DbInit {

    private final ResourceLoader resourceLoader;

    // Spring автоматично впровадить resourceLoader
    public DbInit(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        System.out.println("!!! DbInit bean created !!!");
    }

    @PostConstruct
    public void init() {
        System.out.println("!!! Running database schema initialization !!!");
        try {
            // Використовуємо ResourceLoader для надійного пошуку
            Resource resource = resourceLoader.getResource("classpath:schema.sql");

            if (!resource.exists()) {
                throw new IllegalStateException("schema.sql not found! Перевірте: persistence/src/main/resources/schema.sql");
            }

            try (Connection c = Db.get();
                 Statement st = c.createStatement();
                 var in = resource.getInputStream()) {

                String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                for (String cmd : sql.split(";")) {
                    if (!cmd.isBlank()) {
                        st.execute(cmd);
                    }
                }
                System.out.println("!!! Schema applied successfully !!!");
            }
        } catch (Exception e) {
            throw new RuntimeException("DB schema init failed", e);
        }
    }
}