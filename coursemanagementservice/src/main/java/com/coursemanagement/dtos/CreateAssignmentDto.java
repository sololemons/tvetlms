package com.coursemanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAssignmentDto {

    private Long courseId;
    private String title;
    private String description;
    private String dueDate;

    private int totalMarks;

    private boolean allowDocuments;
    private boolean allowImages;
    private boolean allowVideos;

    private long maxFileSizeMb;
}
