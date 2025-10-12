package com.gradeservice.services;

import com.gradeservice.configuration.RabbitMQConfiguration;
import com.gradeservice.entities.Certifications;
import com.gradeservice.repositories.CertificationsRepository;
import com.shared.dtos.CertificateRequestDto;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final Logger logger = Logger.getLogger(CertificateService.class.getName());
    private final CertificationsRepository certificationsRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final RabbitTemplate rabbitTemplate; // needed to requeue

    @RabbitListener(queues = RabbitMQConfiguration.GENERATE_CERTIFICATE_QUEUE)
    public void generateCertificates(CertificateRequestDto dto) {
        try {
            logger.info("🎓 Generating styled certificate for: " + dto.getStudentFirstName() + " " + dto.getStudentLastName());

            // 1️⃣ Generate styled PDF
            Path pdfPath = generatePdfFromTemplate(dto);
            File pdfFile = pdfPath.toFile();

            // 2️⃣ Create signature and hash
            byte[] pdfBytes = Files.readAllBytes(pdfPath);
            byte[] hash = computeHash(pdfBytes);
            byte[] signature = signData(hash);
            Path sigPath = Path.of(pdfPath.toString().replace(".pdf", ".sig"));
            Files.write(sigPath, signature);

            // 3️⃣ Save certificate in DB
            Certifications cert = new Certifications();
            cert.setAdmissionId(String.valueOf(dto.getStudentId()));
            cert.setCourseName(dto.getCourseName());
            cert.setCertificateFileName(pdfFile.getName());
            certificationsRepository.save(cert);

            // 4️⃣ Send certificate to email
            emailService.sendCertificateEmail(
                    dto.getEmail(),
                    dto.getStudentFirstName() + " " + dto.getStudentLastName(),
                    dto.getCourseName(),
                    pdfFile
            );

            logger.info("✅ Certificate generated, signed, saved, and emailed to: " + dto.getEmail());

        } catch (Exception e) {
            logger.warning("⚠️ Failed to process certificate for: " + dto.getStudentFirstName() + " " + dto.getStudentLastName());
            logger.warning("Reason: " + e.getMessage());

            // ✅ Requeue message for retry
            try {
                rabbitTemplate.convertAndSend(
                        RabbitMQConfiguration.GENERATE_CERTIFICATE_QUEUE,
                        dto
                );
                logger.info("🔁 Requeued certificate generation for retry.");
            } catch (Exception requeueEx) {
                // Prevent infinite loops: don’t requeue again if the requeue itself fails
                logger.severe("❌ Failed to requeue message: " + requeueEx.getMessage());
                throw new AmqpRejectAndDontRequeueException("Permanent failure — message not requeued.");
            }
        }
    }

    private Path generatePdfFromTemplate(CertificateRequestDto dto) throws Exception {
        Path pdfPath = Path.of("certificates/",
                dto.getStudentFirstName().replaceAll(" ", "_") + "_" +
                        dto.getCourseName().replaceAll(" ", "_") + ".pdf");

        Files.createDirectories(pdfPath.getParent());

        // Fill Thymeleaf template
        Context context = new Context();
        context.setVariable("studentName", dto.getStudentFirstName() + " " + dto.getStudentLastName());
        context.setVariable("courseName", dto.getCourseName());
        context.setVariable("completionDate", dto.getCompletionDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));

        String htmlContent = templateEngine.process("certificate-template", context);

        // Render HTML → PDF
        try (FileOutputStream os = new FileOutputStream(pdfPath.toFile())) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, new ClassPathResource("/templates/").getURL().toString());
            builder.toStream(os);
            builder.run();
        }

        return pdfPath;
    }

    private byte[] computeHash(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data);
    }

    private byte[] signData(byte[] data) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initSign(keyPair.getPrivate());
        rsa.update(data);
        return rsa.sign();
    }
}
