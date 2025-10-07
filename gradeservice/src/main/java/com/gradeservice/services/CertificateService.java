package com.gradeservice.services;

import com.gradeservice.configuration.RabbitMQConfiguration;
import com.gradeservice.entities.Certifications;
import com.gradeservice.repositories.CertificationsRepository;
import com.shared.dtos.CertificateRequestDto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

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

    @RabbitListener(queues = RabbitMQConfiguration.GENERATE_CERTIFICATE_QUEUE)
    public void generateCertificates(CertificateRequestDto dto) {
        try {

            Path pdfPath = generatePdf(dto);
            File pdfFile = pdfPath.toFile();


            byte[] pdfBytes = Files.readAllBytes(pdfPath);
            byte[] hash = computeHash(pdfBytes);
            byte[] signature = signData(hash);
            Path sigPath = Path.of(pdfPath.toString().replace(".pdf", ".sig"));
            Files.write(sigPath, signature);

            Certifications cert = new Certifications();
            cert.setAdmissionId(dto.getStudentId());
            cert.setCourseName(dto.getCourseName());
            cert.setCertificateFileName(pdfFile.getName());
            certificationsRepository.save(cert);

            emailService.sendCertificateEmail(
                    dto.getEmail(),
                    dto.getStudentFirstName() + " " + dto.getStudentLastName(),
                    dto.getCourseName(),
                    pdfFile
            );

            logger.info("✅ Certificate generated and emailed to: " + dto.getEmail());

        } catch (Exception e) {
            logger.severe("❌ Error generating or emailing certificate: " + e.getMessage());
        }
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

    private Path generatePdf(CertificateRequestDto dto) throws Exception {
        Path pdfPath = Path.of("certificates/",
                dto.getStudentFirstName().replaceAll(" ", "_") + "_" +
                        dto.getCourseName().replaceAll(" ", "_") + ".pdf");
        Files.createDirectories(pdfPath.getParent());

        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(pdfPath.toFile()));
        doc.open();
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 28, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL);

        Paragraph header = new Paragraph("CERTIFICATE OF COMPLETION", titleFont);
        header.setAlignment(Element.ALIGN_CENTER);
        doc.add(header);

        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("This certifies that " + dto.getStudentFirstName() + " " + dto.getStudentLastName(), normalFont));
        doc.add(new Paragraph("has successfully completed the course:", normalFont));
        doc.add(new Paragraph(dto.getCourseName(), new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD)));
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph("Date of Completion: " + dto.getCompletionDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")), normalFont));
        doc.add(new Paragraph("\nIssued by: Grade Service", normalFont));
        doc.close();

        return pdfPath;
    }
}
