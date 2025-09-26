package com.staffservice.staffservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRequest {
        private String title;
        private String description;
        private String dueDate;
        private List<String> classes;
        private List<QuestionRequest> questions;
}
