package com.adminservice.adminservice.utillis;

import com.adminservice.adminservice.configuration.RabbitMQConfiguration;
import com.adminservice.adminservice.entities.Admin;
import com.adminservice.adminservice.repositories.AdminRepository;
import com.shared.dtos.RequestRoleDto;
import com.shared.dtos.RoleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class CheckRoleService {

    private final AdminRepository adminRepository;

        @RabbitListener(queues = RabbitMQConfiguration.CHECK_ROLE_1)
        public RoleDto checkRole(RequestRoleDto requestRoleDto) {
            String email = requestRoleDto.getEmail();
            Optional<Admin> staff = adminRepository.findByEmail(email);

            if (staff.isPresent()) {
                RoleDto roleDto = new RoleDto();
                roleDto.setEmail(email);
                roleDto.setRole("ADMIN");
                return roleDto;
            }
                return  null;
    }

}
