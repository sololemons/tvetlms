package com.gradeservice.services;

import com.gradeservice.entities.SubmissionGrade;
import org.springframework.data.jpa.domain.Specification;

public class SubmissionGradeSpecification {

    public static Specification<SubmissionGrade> hasCourseId(String courseId) {
        return (root, query, cb) ->
                (courseId == null || courseId.isBlank())
                        ? null
                        : cb.equal(root.get("courseId"), courseId);
    }

    public static Specification<SubmissionGrade> hasStudentAdmissionId(String studentId) {
        return (root, query, cb) ->
                (studentId == null || studentId.isBlank())
                        ? null
                        : cb.equal(root.get("studentAdmissionId"), studentId);
    }

    public static Specification<SubmissionGrade> hasSubmissionType(String type) {
        return (root, query, cb) ->
                (type == null || type.isBlank())
                        ? null
                        : cb.equal(root.get("submissionType"), type);
    }

    public static Specification<SubmissionGrade> hasClassName(String className) {
        return (root, query, cb) ->
                (className == null || className.isBlank())
                        ? null
                        : cb.equal(root.get("className"), className);
    }
    public static Specification<SubmissionGrade> hasSubmissionId(String submissionId) {
        return (root, query, cb) ->
                (submissionId == null || submissionId.isBlank())
                        ? null
                        : cb.equal(root.get("submissionId"), submissionId);
    }
}
