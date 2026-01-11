package sumdu.edu.ua.web;

import sumdu.edu.ua.db.Db;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentDao {

    public void init() throws SQLException {
        String dropSql = "DROP TABLE IF EXISTS comments";
        String createSql = "CREATE TABLE comments (id identity primary key, author varchar(64) not null, text   varchar(1000) not null, created_at timestamp not null default current_timestamp)";
        try (var c = Db.get(); var st = c.createStatement()) {
            st.execute(dropSql);   // Зтираємо стару
            st.execute(createSql); // Створюємо чисту
        }

//        try (var c = Db.get(); var st = c.createStatement()) {
//            st.execute("""
//                create table if not exists comments(
//                  id identity primary key,
//                  author varchar(64) not null,
//                  text   varchar(1000) not null,
//                  created_at timestamp not null default current_timestamp
//                )
//            """);
//        }
    }


    /**
     * Приймає коментар і його автора, після чого додає коментар і повертає згенерований ID.
     */
    public long add(String author, String text) throws SQLException {
        try (var c = Db.get();
             var ps = c.prepareStatement(
                     "insert into comments(author,text) values(?,?)",
                     Statement.RETURN_GENERATED_KEYS
             )) {

            ps.setString(1, author);
            ps.setString(2, text);
            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                throw new SQLException("Failed to retrieve generated ID");
            }
        }
    }


    public List<Map<String, Object>> list() throws SQLException {
        try (var c = Db.get();
             var ps = c.prepareStatement(
                     "select id, author, text, created_at from comments order by id desc"
             );
             var rs = ps.executeQuery()) {

            var out = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
                out.add(Map.of(
                        "id", rs.getLong("id"),
                        "author", rs.getString("author"),
                        "text", rs.getString("text"),
                        "createdAt", rs.getTimestamp("created_at")
                                .toInstant()
                                .toString()
                ));
            }
            return out;
        }
    }
}
