package com.staffservice.staffservice.services;

import com.shared.dtos.*;
import com.staffservice.staffservice.configuration.RabbitMQConfiguration;
import com.staffservice.staffservice.dtos.AssignmentGradeRequestDto;
import com.staffservice.staffservice.entities.Submission;
import com.staffservice.staffservice.entities.SubmissionFile;
import com.staffservice.staffservice.entities.SubmissionStatus;
import com.staffservice.staffservice.exceptions.UserNotFoundException;
import com.staffservice.staffservice.repositories.SubmissionFileRepository;
import com.staffservice.staffservice.repositories.SubmissionRepository;
import com.staffservice.staffservice.retrofit.RetrofitService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViewSubmissionsService {

    private final SubmissionRepository  submissionRepository;
    private final SubmissionFileRepository submissionFileRepository;
    private final RetrofitService retrofitService;
    private final RabbitTemplate rabbitTemplate;
    public List<AssignmentSubmissionResponseDto> getAssignmentSubmissions(Long assignmentId) {

        List<Submission> submissions =
                submissionRepository.findBySubmissionTypeAndTargetId(
                        SubmissionType.ASSIGNMENT,
                        assignmentId
                );

        return submissions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AssignmentSubmissionResponseDto getAssignmentSubmissionById(Long submissionId) {

        Submission submission = submissionRepository
                .findByIdAndSubmissionType(
                        submissionId,
                        SubmissionType.ASSIGNMENT
                )
                .orElseThrow(() ->
                        new RuntimeException("Assignment submission not found")
                );

        return mapToResponse(submission);
    }


    private AssignmentSubmissionResponseDto mapToResponse(Submission submission) {

        List<AssignmentFileDto> files =
                submissionFileRepository.findBySubmissionId(submission.getId())
                        .stream()
                        .map(file -> {
                            AssignmentFileDto dto = new AssignmentFileDto();
                            dto.setFileId(file.getFileId());
                            dto.setFileName(file.getFileName());
                            dto.setFileType(file.getFileType());
                            dto.setFileUrl(file.getFileUrl());
                            return dto;
                        })
                        .collect(Collectors.toList());

        AssignmentSubmissionResponseDto dto = getAssignmentSubmissionResponseDto(submission, files);

        return dto;
    }

    private static AssignmentSubmissionResponseDto getAssignmentSubmissionResponseDto(Submission submission, List<AssignmentFileDto> files) {
        AssignmentSubmissionResponseDto dto = new AssignmentSubmissionResponseDto();
        dto.setSubmissionId(submission.getId());
        dto.setStudentAdmissionId(submission.getStudentAdmissionId());
        dto.setClassName(submission.getClassName());
        dto.setCourseId((long) submission.getCourseId());
        dto.setModuleId((long) submission.getModuleId());
        dto.setTargetId(submission.getTargetId());
        dto.setSubmissionStatus(submission.getSubmissionStatus().name());
        dto.setSubmissionDate(LocalDateTime.parse(submission.getSubmissionDate()));
        dto.setFiles(files);
        return dto;
    }

    public AssignmentGradeResponseDto gradeAssignments(AssignmentGradeRequestDto request) {
        Submission submission = submissionRepository.findById(Long.valueOf(request.getSubmissionId())).orElseThrow(()
                ->new UserNotFoundException("Submission Not found"));

        if (submission.getSubmissionType() != SubmissionType.ASSIGNMENT) {
            throw new RuntimeException("Not an assignment submission");
        }
        AssignmentDto assignmentDto = retrofitService.getAssignments(submission.getTargetId(),submission.getCourseId());

        AssessmentDetails assessmentDetails = new AssessmentDetails();
        assessmentDetails.setTargetId((int) submission.getTargetId());
        assessmentDetails.setAssessmentType(String.valueOf(SubmissionType.ASSIGNMENT));
        assessmentDetails.setCourseId(submission.getCourseId());
        assessmentDetails.setClassName(submission.getClassName());

        AssignmentGradeResponseDto dto = new AssignmentGradeResponseDto();
        dto.setAssessmentDetails(assessmentDetails);
        dto.setSubmissionId(submission.getId());
        dto.setAdmissionId(submission.getStudentAdmissionId());
        dto.setGradedAt(String.valueOf(LocalDateTime.now()));
        dto.setFeedback(request.getFeedback());
        dto.setAwardedMarks(request.getAwardedMarks());
        dto.setMaxMarks(assignmentDto.getTotalMarks());

        rabbitTemplate.convertAndSend(RabbitMQConfiguration.ASSIGNMENT_GRADING_QUEUE,dto);


        return dto;

    }

    public GradedAssignmentDetailsDto getGradedAssignments(Long submissionId) {
        Submission submission = submissionRepository.findByIdAndSubmissionTypeAndSubmissionStatus
                (submissionId,SubmissionType.ASSIGNMENT, SubmissionStatus.GRADED).orElseThrow(()
                ->new UserNotFoundException("Submission Not found"));
        List<SubmissionFile> submissionFile = submissionFileRepository.findBySubmissionId(submissionId);
        AssignmentDto assignmentDto = retrofitService.getAssignments(submission.getTargetId(),submission.getCourseId());
        List<AssignmentFileDto> assignmentFiles = submissionFile.stream()
                .map(file -> {
                    AssignmentFileDto dto = new AssignmentFileDto();
                    dto.setFileId(file.getFileId());
                    dto.setFileName(file.getFileName());
                    dto.setFileType(file.getFileType());
                    dto.setFileUrl(file.getFileUrl());
                    return dto;
                })
                .toList();

        SubmissionGradeDto submissionGradeDto = retrofitService.getSubmissionGradesBySubmissionId(submissionId);
         GradedAssignmentDetailsDto gradedAssignmentDetailsDto = new GradedAssignmentDetailsDto();
         gradedAssignmentDetailsDto.setAssignmentDescription(assignmentDto.getAssignmentDescription());
         gradedAssignmentDetailsDto.setAssignmentName(assignmentDto.getAssignmentName());
         gradedAssignmentDetailsDto.setAwardedMarks(submissionGradeDto.getTotalPoints());
         gradedAssignmentDetailsDto.setPercentage(submissionGradeDto.getPercentage());
         gradedAssignmentDetailsDto.setMaxMarks(submissionGradeDto.getMaxPoints());
         gradedAssignmentDetailsDto.setFiles(assignmentFiles);

         return gradedAssignmentDetailsDto;
    }
}
