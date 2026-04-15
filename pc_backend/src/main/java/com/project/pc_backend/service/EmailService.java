package com.project.pc_backend.service;

import com.project.pc_backend.dto.EmailDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public void sendMail(EmailDetails emailDetails){
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(emailDetails.getSender());
        smm.setTo(emailDetails.getRecipient());
        smm.setReplyTo(emailDetails.getSender());
        smm.setText(emailDetails.getMessageBody());
        smm.setSubject(emailDetails.getSubject());

        javaMailSender.send(smm);
    }
}
