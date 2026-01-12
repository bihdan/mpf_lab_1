package sumdu.edu.ua.web.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sumdu.edu.ua.core.domain.Book;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    private final String baseUrl;
    private final String adminEmail;
    private final String apiKey;
    private final String fromAddress;

    private final EmailTemplateProcessor templateProcessor;
    private final RestTemplate restTemplate = new RestTemplate();

    public MailService(
            EmailTemplateProcessor templateProcessor,
            @Value("${app.base-url}") String baseUrl,
            @Value("${app.mail.admin}") String adminEmail,
            @Value("${mail.resend.api-key}") String apiKey,
            @Value("${mail.resend.from}") String fromAddress) {

        this.templateProcessor = templateProcessor;
        this.baseUrl = baseUrl;
        this.adminEmail = adminEmail;
        this.apiKey = apiKey;
        this.fromAddress = fromAddress;

        log.info("MailService initialized. API Key length: {}", (this.apiKey != null ? this.apiKey.length() : "NULL"));
    }

    public void sendVerificationEmail(String email, String token) {
        Map<String, Object> model = new HashMap<>();
        String confirmUrl = baseUrl + "/verify?token=" + token;

        model.put("confirmUrl", confirmUrl);
        model.put("email", email);

        String html = templateProcessor.processTemplate("verify.ftl", model);

        sendHtml(email, "Підтвердження реєстрації", html);
    }

    public void sendNewBookEmail(Book book) {
        Map<String, Object> model = new HashMap<>();
        model.put("appBaseUrl", baseUrl);
        model.put("id", book.getId());
        model.put("title", book.getTitle());
        model.put("author", book.getAuthor());
        model.put("year", book.getPubYear());
        model.put("comments", null);
        model.put(
                "createdAt",
                Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
        );

        String html = templateProcessor.processTemplate("new_book.ftl", model);
        sendHtml(adminEmail, "Нова книга в каталозі: " + book.getTitle(), html);
    }

    private void sendHtml(String to, String subject, String html) {
        if (apiKey == null || apiKey.isBlank() || apiKey.contains("ключ з Resend")) {
            log.error("Resend API key is NOT configured properly!");
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("from", fromAddress);
            body.put("to", List.of(to));
            body.put("subject", subject);
            body.put("html", html);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(RESEND_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("SUCCESS: Email sent via Resend API to {} with subject '{}'", to, subject);
            } else {
                log.error("RESEND ERROR: Status code: {}, Body: {}", response.getStatusCode().value(), response.getBody());
            }
        } catch (Exception e) {
            log.error("CRITICAL ERROR: Failed to send email to {}", to, e);
        }
    }
}