package com.project.pc_backend.service;

import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    public String contactTemplate(String name, String email, String message) {
        String escName = escapeHtml(name);
        String escEmail = escapeHtml(email);
        String escMessage = escapeHtml(message).replace("\n", "<br/>");

        return """
    <div style="font-family: Arial, sans-serif; padding:24px; background:#f2f4f8; color:#0f172a;">
        <div style="max-width:600px; margin:auto; background:#ffffff; padding:24px; border-radius:18px; box-shadow:0 18px 40px rgba(15, 23, 42, 0.08);">
            <div style="margin-bottom:24px;">
                <p style="margin:0; color:#334155; font-size:14px; letter-spacing:0.03em; text-transform:uppercase;">Campaign contact message</p>
                <h1 style="margin:8px 0 0; font-size:24px; color:#0f172a;">New message from your website</h1>
            </div>

            <div style="background:#f8fafc; border:1px solid #e2e8f0; border-radius:12px; padding:18px; margin-bottom:20px;">
                <p style="margin:0 0 12px; font-size:16px; color:#334155;"><strong>Name:</strong> %s</p>
                <p style="margin:0 0 12px; font-size:16px; color:#334155;"><strong>Email:</strong> %s</p>
            </div>

            <div style="padding:18px 20px; border-radius:12px; background:#eef2ff; border:1px solid #c7d2fe;">
                <p style="margin:0 0 12px; font-size:16px; color:#0f172a;"><strong>Message</strong></p>
                <p style="margin:0; font-size:15px; line-height:1.7; color:#334155;">%s</p>
            </div>

            <div style="margin-top:24px; font-size:13px; color:#64748b;">
                <p style="margin:0;">Reply to the visitor at <a href=\"mailto:%s\" style=\"color:#2563eb; text-decoration:none;\">%s</a>.</p>
            </div>
        </div>
    </div>
    """.formatted(escName, escEmail, escMessage, escEmail, escEmail);
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
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


// Legacy JavaMail example removed to avoid printing to stdout. Use ResendEmailService instead.
