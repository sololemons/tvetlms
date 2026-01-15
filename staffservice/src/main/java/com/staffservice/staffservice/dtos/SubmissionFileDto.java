package com.staffservice.staffservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionFileDto {

    private Long fileId;
    private String fileName;
    private String fileType;
    private String fileUrl;
}
