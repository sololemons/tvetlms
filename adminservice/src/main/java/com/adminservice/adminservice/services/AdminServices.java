package com.adminservice.adminservice.services;

import com.adminservice.adminservice.configuration.RabbitMQConfiguration;
import com.adminservice.adminservice.dtos.AdminDto;
import com.adminservice.adminservice.dtos.Role;
import com.adminservice.adminservice.entities.Admin;
import com.adminservice.adminservice.repositories.AdminRepository;
import com.shared.dtos.StaffPayload;
import com.shared.dtos.StudentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServices {
    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;



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

}
