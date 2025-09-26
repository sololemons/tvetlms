package com.adminservice.adminservice.controllers;

import com.adminservice.adminservice.dtos.AdminDto;
import com.adminservice.adminservice.services.AdminServices;
import com.shared.dtos.StaffPayload;
import com.shared.dtos.StudentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminServices adminService;

    @PostMapping("/add/student")
    public void addStudent(@RequestBody StudentDto studentDto) {
        adminService.addStudent(studentDto);
    }

    @PostMapping("/add/staff")
    public void addStaff(@RequestBody StaffPayload staffDto) {
        adminService.addStaff(staffDto);
    }

    @GetMapping("/get")
    public ResponseEntity<List<AdminDto>> getAdmin() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

}
