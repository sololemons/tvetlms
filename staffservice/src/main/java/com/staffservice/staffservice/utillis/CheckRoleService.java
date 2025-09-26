package com.staffservice.staffservice.utillis;

import com.shared.dtos.RequestRoleDto;
import com.shared.dtos.RoleDto;
import com.staffservice.staffservice.configuration.RabbitMQConfiguration;
import com.staffservice.staffservice.entities.Staff;
import com.staffservice.staffservice.repositories.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CheckRoleService {

    private final StaffRepository staffRepository;
    @RabbitListener(queues = RabbitMQConfiguration.CHECK_ROLE_1)
    public RoleDto checkRole(RequestRoleDto requestRoleDto) {
        String email = requestRoleDto.getEmail();
        Optional<Staff> staff = staffRepository.findByEmail(email);

        if (staff.isPresent()) {
            RoleDto roleDto = new RoleDto();
            roleDto.setEmail(email);
            roleDto.setRole("STAFF");
            return roleDto;
        }
        return null;
    }
}
