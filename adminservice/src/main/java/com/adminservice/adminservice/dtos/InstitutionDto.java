package com.adminservice.adminservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionDto {
    private String institutionName;
    private String county;
    private MultipartFile signatureFile;
}
