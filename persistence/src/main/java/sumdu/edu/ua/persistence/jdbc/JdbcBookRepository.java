package sumdu.edu.ua.persistence.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import org.springframework.stereotype.Repository;


import java.sql.*;
import java.util.ArrayList;

@Repository
public class JdbcBookRepository implements CatalogRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(JdbcBookRepository.class);

    @Override
    public Page<Book> search(String q, PageRequest request) {
        var items = new ArrayList<Book>();
        long total = 0;

        // 1. Отримуємо поле для сортування з PageRequest (sortBy)
        String sortField = request.getSortBy();
        // Перевірка на безпеку (щоб не було SQL Injection)
        java.util.List<String> allowed = java.util.List.of("id", "title", "author", "pub_year");
        if (sortField == null || !allowed.contains(sortField.toLowerCase())) {
            sortField = "id";
        }

        // 2. Спільна умова WHERE для обох запитів
        String whereClause = " WHERE 1=1";
        if (q != null && !q.isBlank()) {
            whereClause += " AND (LOWER(title) LIKE ? OR LOWER(author) LIKE ?)";
        }

        // 3. Основний запит з динамічним ORDER BY
        String sql = "SELECT id, title, author, pub_year FROM books" + whereClause +
                " ORDER BY " + sortField + " LIMIT ? OFFSET ?";

        try (Connection c = Db.get()) {
            // Запит на дані
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                int i = 1;
                if (q != null && !q.isBlank()) {
                    String pattern = "%" + q.toLowerCase() + "%";
                    ps.setString(i++, pattern);
                    ps.setString(i++, pattern);
                }
                ps.setInt(i++, request.getSize());
                ps.setInt(i, request.getPage() * request.getSize());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        items.add(new Book(rs.getLong("id"), rs.getString("title"),
                                rs.getString("author"), rs.getInt("pub_year")));
                    }
                }
            }

            // 4. Підрахунок TOTAL (обов'язково з тією ж умовою WHERE!)
            String countSql = "SELECT COUNT(*) FROM books" + whereClause;
            try (PreparedStatement countPs = c.prepareStatement(countSql)) {
                if (q != null && !q.isBlank()) {
                    String pattern = "%" + q.toLowerCase() + "%";
                    countPs.setString(1, pattern);
                    countPs.setString(2, pattern);
                }
                try (ResultSet rs = countPs.executeQuery()) {
                    if (rs.next()) {
                        total = rs.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB query error", e);
        }
        return new Page<>(items, request, total);
    }

    @Override
    public Book findById(long id) {
        try (var c = Db.get();
             var ps = c.prepareStatement(
                     "select id, title, author, pub_year from books where id = ?")) {
            ps.setLong(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Book(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("pub_year")
                    );
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("DB query error", e);
        }
    }

    @Override
    public Book add(String title, String author, int pubYear) {
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO books(title, author, pub_year) VALUES (?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, pubYear);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return new Book(id, title, author, pubYear);
                }
            }
            throw new RuntimeException("Insert succeeded but no ID generated");
        } catch (SQLException e) {
            throw new RuntimeException("DB insert book failed", e);
        }
    }



}
