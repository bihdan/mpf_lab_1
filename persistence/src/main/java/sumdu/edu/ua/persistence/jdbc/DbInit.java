package sumdu.edu.ua.persistence.jdbc;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

@Component
public final class DbInit {

    public DbInit() {
        System.out.println("!!! DbInit bean created !!!");
    }

    @PostConstruct
    public void init() {
        System.out.println("!!! Running database schema initialization !!!");
        try (Connection c = Db.get();
             Statement st = c.createStatement()) {

            try (var in = DbInit.class.getClassLoader().getResourceAsStream("schema.sql")) {
                if (in == null) {
                    throw new IllegalStateException("schema.sql not found in resources");
                }
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