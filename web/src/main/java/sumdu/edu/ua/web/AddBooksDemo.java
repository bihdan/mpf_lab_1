package sumdu.edu.ua.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import sumdu.edu.ua.core.domain.Book;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AddBooksDemo {
    public static void main(String[] args) throws Exception {
        var books = List.of(
                new Book(1L, "The Hobbit", "J.R.R. Tolkien", 1937),
                new Book(2L, "It", "Stephen King", 1986),
                new Book(3L, "1984", "George Orwell", 1949)
        );

        var mapper = new ObjectMapper();
        for (Book b : books) {
            URL url = new URL("http://localhost:8080/api/books");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                os.write(mapper.writeValueAsBytes(b));
            }

            System.out.println("→ " + b.getTitle() + " : " + con.getResponseCode());
            con.disconnect();
        }

        System.out.println("\nCatalog:");
        System.out.println(new String(
                new URL("http://localhost:8080/api/books").openStream().readAllBytes(),
                StandardCharsets.UTF_8
        ));
    }
}