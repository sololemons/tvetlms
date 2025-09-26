package com.coursemanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDto {
    private String week;
    private String moduleName;
    private String content;
}
