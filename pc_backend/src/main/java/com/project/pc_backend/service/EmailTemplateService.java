package com.project.pc_backend.service;

import com.project.pc_backend.dto.EmailDetails;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailTemplateService {

    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public String contactTemplate(String name, String email, String message) {
        return """
    <div style="font-family: Arial; padding:20px; background:#f9fafb;">
        <div style="max-width:600px; margin:auto; background:white; padding:20px; border-radius:10px;">
            
            <h2 style="color:#2f9e44;">📩 New Contact Message</h2>

            <p><strong>Name:</strong> %s</p>
            <p><strong>Email:</strong> %s</p>

            <hr style="margin:20px 0;"/>

            <p style="line-height:1.6;">%s</p>

        </div>
    </div>
    """.formatted(name, email, message);
    }

    public String adminWelcomeTemplate(String username) {
        return """
    <div style="font-family: Arial; padding:20px; background:#030712; color:white;">
        <div style="max-width:600px; margin:auto; padding:20px; border-radius:10px; background:#0f172a;">
            
            <h2 style="color:#5dade2;">Welcome to Oroye Campaign</h2>

            <p>Hello %s,</p>

            <p>You now have <strong>Admin Access</strong>.</p>

            <ul>
                <li>Add events</li>
                <li>Manage timeline</li>
                <li>Control campaign content</li>
            </ul>

            <p style="margin-top:20px;">
                Access your dashboard via <strong>/admin</strong>
            </p>

            <hr style="margin:20px 0; border-color:#1e293b;"/>

            <p style="font-size:12px; color:#94a3b8;">
                Keep your credentials secure.
            </p>

        </div>
    </div>
    """.formatted(username);
    }

}


/*

try {
            SimpleMailMessage smm = new SimpleMailMessage();
            smm.setFrom(mailUsername);
            smm.setTo(emailDetails.getRecipient());
            smm.setReplyTo(emailDetails.getSender());
            smm.setText(emailDetails.getMessageBody());
            smm.setSubject(emailDetails.getSubject());

            javaMailSender.send(smm);

            System.out.println("EMAIL SENT SUCCESSFULLY");

        } catch (Exception e) {
            System.out.println("EMAIL FAILED:");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
 */
