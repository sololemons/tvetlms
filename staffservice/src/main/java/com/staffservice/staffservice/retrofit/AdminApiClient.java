package com.staffservice.staffservice.retrofit;


import com.shared.dtos.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface AdminApiClient {


    @GET("/course/get/catAssessment")
    Call<CatAssessmentResponseDto> getCatAssessment(
            @Query("courseId") Integer courseId,
            @Query("catId") Integer catId
    );
    @GET("/course/get/quizAssessment")
    Call<QuizAssessmentResponseDto>getQuizAssessment(
            @Query("courseId") Integer courseId,
            @Query("moduleId") Integer moduleId,
            @Query("quizId") Integer quizId
    );
    @GET("/grade/filter/grades")
    Call<List<SubmissionGradeDto>> getFilteredSubmissionGrades(
            @Query("courseId") String courseId,
            @Query("submissionType") String submissionType,
            @Query("studentAdmissionId") String studentAdmissionId,
            @Query("className") String className
    );
    @GET("/grade/get/grades/submissionid")
    Call<SubmissionGradeDto> getSubmissionGradesBySubmissionId(
            @Query("submissionId") Long submissionId

    );

}
