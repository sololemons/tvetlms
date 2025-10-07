package com.gradeservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.io.File;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendCertificateEmail(String toEmail, String studentName, String courseName, File certificateFile) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("🎓 Certificate of Completion — " + courseName);
            helper.setText(
                    "<p>Dear " + studentName + ",</p>" +
                            "<p>Congratulations on successfully completing <b>" + courseName + "</b>!</p>" +
                            "<p>Please find your certificate attached below.</p>" +
                            "<br><p>Best regards,<br><b>Grade Service</b></p>",
                    true
            );

            helper.addAttachment(certificateFile.getName(), new FileSystemResource(certificateFile));
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
