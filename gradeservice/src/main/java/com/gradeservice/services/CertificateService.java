package com.gradeservice.services;

import com.gradeservice.configuration.RabbitMQConfiguration;
import com.gradeservice.entities.Certifications;
import com.gradeservice.repositories.CertificationsRepository;
import com.gradeservice.retrofit.RetrofitService;
import com.shared.dtos.CertificateRequestDto;
import com.shared.dtos.SignatureDto;
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
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final Logger logger = Logger.getLogger(CertificateService.class.getName());
    private final CertificationsRepository certificationsRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final RabbitTemplate rabbitTemplate;
    private final RetrofitService retrofitService;

    @RabbitListener(queues = RabbitMQConfiguration.GENERATE_CERTIFICATE_QUEUE)
    public void generateCertificates(CertificateRequestDto dto) {
        try {
            logger.info("🎓 Generating certificate for: " +
                    dto.getStudentFirstName() + " " + dto.getStudentLastName());

            SignatureDto vocalLearnSignature = retrofitService.getVocalLearnSignature();
            SignatureDto institutionSignature = retrofitService.getSignature();

            Path pdfPath = generatePdfFromTemplate(
                    dto,
                    vocalLearnSignature.getSignature(),
                    institutionSignature.getSignature()
            );

            File pdfFile = pdfPath.toFile();

            Certifications cert = new Certifications();
            cert.setAdmissionId(String.valueOf(dto.getStudentId()));
            cert.setCourseName(dto.getCourseName());
            cert.setCertificateFileName(pdfFile.getName());
            certificationsRepository.save(cert);

            emailService.sendCertificateEmail(
                    dto.getEmail(),
                    dto.getStudentFirstName() + " " + dto.getStudentLastName(),
                    dto.getCourseName(),
                    pdfFile
            );

            logger.info("✅ Certificate generated and emailed successfully");

        } catch (Exception e) {
            logger.warning("⚠️ Certificate generation failed: " + e.getMessage());

            try {
                rabbitTemplate.convertAndSend(
                        RabbitMQConfiguration.GENERATE_CERTIFICATE_QUEUE, dto);
            } catch (Exception ex) {
                throw new AmqpRejectAndDontRequeueException(
                        "Permanent failure — not requeuing");
            }
        }
    }

    private Path generatePdfFromTemplate(
            CertificateRequestDto dto,
            String vocalLearnSignaturePath,
            String institutionSignaturePath
    ) throws Exception {

        Path pdfPath = Path.of(
                "certificates",
                dto.getStudentFirstName().replaceAll(" ", "_") + "_" +
                        dto.getCourseName().replaceAll(" ", "_") + ".pdf"
        );

        Files.createDirectories(pdfPath.getParent());

        Context context = new Context();
        context.setVariable("studentName",
                dto.getStudentFirstName() + " " + dto.getStudentLastName());
        context.setVariable("courseName", dto.getCourseName());
        context.setVariable("completionDate",
                dto.getCompletionDate().format(
                        DateTimeFormatter.ofPattern("dd MMMM yyyy")));

        context.setVariable(
                "vocalLearnSignaturePath", toFileUri(vocalLearnSignaturePath));
        context.setVariable(
                "institutionSignaturePath", toFileUri(institutionSignaturePath));

        String html = templateEngine.process("certificate-template", context);

        try (FileOutputStream os = new FileOutputStream(pdfPath.toFile())) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(
                    html,
                    new ClassPathResource("templates/").getURL().toString()
            );
            builder.toStream(os);
            builder.run();
        }

        return pdfPath;
    }

    private String toFileUri(String path) {
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File("E:/tvetlms/adminservice/" + path);
        }
        return file.getAbsoluteFile().toURI().toString();
    }
}
