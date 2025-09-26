package com.staffservice.staffservice.dtos;

import com.staffservice.staffservice.entities.Assignments;
import com.staffservice.staffservice.entities.Class;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class AssignmentMapper {

    public static AssignmentDto toDto(Assignments assignment) {
        AssignmentDto dto = new AssignmentDto();
        dto.setAssignmentId(assignment.getAssignmentId());
        dto.setTitle(assignment.getTitle());
        dto.setCreationDate(assignment.getCreationDate());
        dto.setDueDate(String.valueOf(assignment.getDueDate()));
        dto.setDescription(assignment.getDescription());
        dto.setClassNames(
                assignment.getClasses()
                        .stream()
                        .distinct()
                        .map(Class::getClassName)
                        .collect(Collectors.toList())
        );



        dto.setQuestions(
                assignment.getQuestions()
                        .stream()
                        .distinct()
                        .map(q -> new QuestionDto(q.getQuestionId(), q.getQuestionText(), String.valueOf(q.getQuestionType()),  q.getOptions()))
                        .collect(Collectors.toList())
        );

        return dto;
    }
}
