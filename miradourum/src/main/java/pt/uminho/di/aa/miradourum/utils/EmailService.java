package pt.uminho.di.aa.miradourum.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String comment) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();

        // true = multipart message
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Your Point of Interest Was Rejected");

        String htmlContent = "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd;'>" +
                "<img src='cid:logoImage' style='width: 150px; display: block; margin-bottom: 20px;'/>" +
                "<h2 style='color: #c0392b;'>Point of Interest Rejected</h2>" +
                "<p>Hello,</p>" +
                "<p>Your submitted point of interest was <strong>rejected</strong> for the following reason:</p>" +
                "<blockquote style='border-left: 4px solid #c0392b; margin: 10px 0; padding-left: 15px; color: #555;'>" +
                comment +
                "</blockquote>" +
                "<p>We appreciate your interest in contributing to <strong>Miradourum</strong>.</p>" +
                "<p>Regards,<br/>The Miradourum Team</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        helper.setText(htmlContent, true);

        // Embed the logo
        ClassPathResource logoImage = new ClassPathResource("/static/images/logo.png"); // Make sure this path is correct
        helper.addInline("logoImage", logoImage);
        mailSender.send(message);
    }
}
