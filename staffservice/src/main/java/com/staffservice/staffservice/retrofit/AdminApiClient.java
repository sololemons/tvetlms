package com.staffservice.staffservice.retrofit;


import com.shared.dtos.ModuleDto;
import com.shared.dtos.QuizAssessmentDto;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface AdminApiClient {


    @GET("/course/catAssessment/{courseId}/{quizId}")
    Call<List<CatAssessmentDto>> getCatAssessment(
            @Path("courseId") Integer courseId,
            @Path("catId") Integer catId
    );
    @GET("/course/quizAssessment/{courseId}/{quizId}/{moduleId}")
    Call<List<QuizAssessmentDto>> getQuizAssessment(
            @Path("courseId") Integer courseId,
            @Path("catId") Integer catId,
            @Path("moduleId") Integer moduleId
    );

}
