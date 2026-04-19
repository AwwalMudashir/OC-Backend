package com.project.pc_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class ResendEmailService {

    @Value("${resend_api_key}")
    private String apiKey;

    @Value("${resend_email_from}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String html) {
        try {
            URL url = new URL("https://api.resend.com/emails");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String body = """
            {
              "from": "%s",
              "to": ["%s"],
              "subject": "%s",
              "html": "%s"
            }
            """.formatted(
                    fromEmail,
                    to,
                    subject,
                    html.replace("\"", "\\\"")
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();

            if (responseCode >= 400) {
                throw new RuntimeException("Resend failed with code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Email sending failed");
        }
    }
}