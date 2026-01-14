package com.adminservice.adminservice.services;

import com.adminservice.adminservice.configuration.RabbitMQConfiguration;
import com.adminservice.adminservice.dtos.AdminDto;
import com.adminservice.adminservice.dtos.InstitutionDto;
import com.adminservice.adminservice.dtos.Role;
import com.adminservice.adminservice.entities.Admin;
import com.adminservice.adminservice.entities.Institution;
import com.adminservice.adminservice.entities.VocalLearnSignature;
import com.adminservice.adminservice.repositories.AdminRepository;
import com.adminservice.adminservice.repositories.InstitutionRepository;
import com.adminservice.adminservice.repositories.VocalLearnRepository;
import com.shared.dtos.SignatureDto;
import com.shared.dtos.StaffPayload;
import com.shared.dtos.StudentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServices {
    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;
    private final InstitutionRepository institutionRepository;
    private final VocalLearnRepository vocalLearnRepository;


    public void addStudent(StudentDto studentDto) {

        rabbitTemplate.convertAndSend(RabbitMQConfiguration.ADD_STUDENT_QUEUE, studentDto);

    }

    public void addStaff(StaffPayload staffDto) {

        rabbitTemplate.convertAndSend(RabbitMQConfiguration.ADD_STAFF_QUEUE, staffDto);

    }

    public List<AdminDto> getAllAdmins() {
        List<Admin> admin = adminRepository.findAll();
        return admin.stream()
                .map(d -> {
                    AdminDto dto = new AdminDto();
                    dto.setFirstName(d.getFirstName());
                    dto.setLastName(d.getLastName());
                    dto.setEmail(d.getEmail());
                    return dto;

                })
                .collect(Collectors.toList());
    }
    public String addInstitution(InstitutionDto institutionDto) {
        try {
            MultipartFile file = institutionDto.getSignatureFile();
            String uploadDir = "uploads/signatures/";

            Files.createDirectories(Paths.get(uploadDir));

            String filePath = uploadDir + file.getOriginalFilename();
            Path path = Paths.get(filePath);
            file.transferTo(path);

            Institution institution = new Institution();
            institution.setInstitutionName(institutionDto.getInstitutionName());
            institution.setCounty(institutionDto.getCounty());
            institution.setSignature(filePath);

            institutionRepository.save(institution);

            return "Institution added successfully with signature.";
        } catch (IOException e) {
            throw new RuntimeException("Failed to save institution ", e);
        }
    }

    public SignatureDto getSignature() {

        Institution institution = institutionRepository.findAll().getFirst();

        SignatureDto signatureDto = new SignatureDto();

        assert institution != null;

        signatureDto.setSignature(institution.getSignature());

        return signatureDto;

    }
    public String addSignature(MultipartFile signatureFile) {
        try {
            String uploadDir = "uploads/signatures/";

            Files.createDirectories(Paths.get(uploadDir));

            String filePath = uploadDir + signatureFile.getOriginalFilename();
            Path path = Paths.get(filePath);
            signatureFile.transferTo(path);
            VocalLearnSignature vocalLearnSignature = new VocalLearnSignature();
            vocalLearnSignature.setSignature(filePath);


            vocalLearnRepository.save(vocalLearnSignature);

            return "Vocallearn Signature added";
        } catch (IOException e) {
            throw new RuntimeException("Failed to save signature ", e);
        }
    }
    public SignatureDto getVocalLearnSignature() {

        VocalLearnSignature vocalLearnSignature = vocalLearnRepository.findAll().getFirst();

        SignatureDto signatureDto = new SignatureDto();

        assert vocalLearnSignature != null;

        signatureDto.setSignature(vocalLearnSignature.getSignature());

        return signatureDto;

    }
}
