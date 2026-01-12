package sumdu.edu.ua.persistence.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

import java.sql.*;
import java.util.ArrayList;

public class JdbcBookRepository implements CatalogRepositoryPort {
    private static final Logger log = LoggerFactory.getLogger(JdbcBookRepository.class);

    @Override
    public Page<Book> search(String q, PageRequest request) {
        var items = new ArrayList<Book>();
        long total = 0;

        String sortColumn = request.getSortBy();
        if (sortColumn == null || (!sortColumn.equals("title") && !sortColumn.equals("author") && !sortColumn.equals("pub_year"))) {
            sortColumn = "id";
        }

        // 1. Запит на отримання даних
        String sql = "select id, title, author, pub_year from books where 1=1";
        if (q != null && !q.isBlank()) {
            sql += " and (lower(title) like ? or lower(author) like ?)";
        }
        sql += " order by " + sortColumn + " asc limit ? offset ?";

        try (var c = Db.get()) {
            try (var ps = c.prepareStatement(sql)) {
                int i = 1;
                if (q != null && !q.isBlank()) {
                    String pattern = "%" + q.toLowerCase() + "%";
                    ps.setString(i++, pattern);
                    ps.setString(i++, pattern);
                }
                ps.setInt(i++, request.getSize());
                ps.setInt(i, request.getPage() * request.getSize());

                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        items.add(new Book(rs.getLong("id"), rs.getString("title"),
                                rs.getString("author"), rs.getInt("pub_year")));
                    }
                }
            }

            // 2. Виправлений запит на підрахунок total (має враховувати фільтр q)
            String countSql = "select count(*) from books where 1=1";
            if (q != null && !q.isBlank()) {
                countSql += " and (lower(title) like ? or lower(author) like ?)";
            }
            try (var countPs = c.prepareStatement(countSql)) {
                if (q != null && !q.isBlank()) {
                    String pattern = "%" + q.toLowerCase() + "%";
                    countPs.setString(1, pattern);
                    countPs.setString(2, pattern);
                }
                try (var rs = countPs.executeQuery()) {
                    if (rs.next()) total = rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            log.error("Database search error", e);
            throw new RuntimeException("DB query error", e);
        }
        return new Page<>(items, request, total);
    }

    // add() та findById() залишаються без змін...
    @Override
    public Book add(String title, String author, int pubYear) {
        try (Connection c = Db.get();
             PreparedStatement check = c.prepareStatement(
                     "SELECT id FROM books WHERE lower(title) = lower(?) AND lower(author) = lower(?)")) {
            check.setString(1, title);
            check.setString(2, author);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) throw new IllegalStateException("Book already exists");
            }
        } catch (SQLException e) { throw new RuntimeException(e); }

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO books(title, author, pub_year) VALUES (?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, pubYear);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return new Book(keys.getLong(1), title, author, pubYear);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public Book findById(long id) {
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM books WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Book(rs.getLong("id"), rs.getString("title"),
                        rs.getString("author"), rs.getInt("pub_year"));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }
}