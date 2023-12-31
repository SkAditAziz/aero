package dev.example.aero.service.utilService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmailWithAttachment(String to, String sub, String body, String filePath) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(sub);
            helper.setText(body);

            FileSystemResource file = new FileSystemResource(new File(filePath));

            String[] fileNames = filePath.split("/");
            helper.addAttachment(fileNames[fileNames.length - 1], file);

            javaMailSender.send(message);

        } catch (MailException | MessagingException me) {
            me.printStackTrace();
        }
    }
}
