package com.staffservice.staffservice.retrofit;


import com.shared.dtos.CatAssessmentResponseDto;
import com.shared.dtos.ModuleDto;
import com.shared.dtos.QuizAssessmentDto;
import com.shared.dtos.QuizAssessmentResponseDto;
import retrofit2.Call;
import retrofit2.http.*;

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

}
