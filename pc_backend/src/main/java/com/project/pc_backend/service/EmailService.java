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
public class EmailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public void sendMail(EmailDetails emailDetails){
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(mailUsername);
            helper.setTo(emailDetails.getRecipient());
            helper.setReplyTo(emailDetails.getSender());
            helper.setSubject(emailDetails.getSubject());

            String html = """
            <div style="font-family: Arial; padding:20px;">
                <h2 style="color:#2f9e44;">New Contact Message</h2>
                <p><strong>Email:</strong> %s</p>
                <p style="margin-top:20px;">%s</p>
            </div>
        """.formatted(
                    emailDetails.getSender(),
                    emailDetails.getMessageBody()
            );

            helper.setText(html, true); // TRUE = HTML

            javaMailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("HTML Email failed");
        }
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
