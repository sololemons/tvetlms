package com.staffservice.staffservice.retrofit;

import com.shared.dtos.CatAssessmentResponseDto;
import com.shared.dtos.QuizAssessmentResponseDto;
import com.staffservice.staffservice.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;


@Service
public class RetrofitService {
    private static final Logger logger = LoggerFactory.getLogger(RetrofitService.class);
    private final AdminApiClient apiClient;

    public RetrofitService(RetrofitClient retrofitClient) {
        this.apiClient = retrofitClient.getClient().create(AdminApiClient.class);
    }


    public QuizAssessmentResponseDto getQuizAssessment(Integer courseId, Integer moduleId, Integer quizId) {
        logger.info("Fetching quiz assessment for courseId={}, moduleId={}, quizId={}", courseId, moduleId, quizId);
        Call<QuizAssessmentResponseDto> call = apiClient.getQuizAssessment(courseId, moduleId, quizId);
        try {
            Response<QuizAssessmentResponseDto> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                logger.info("Successfully fetched quiz assessment. Response code: {}", response.code());
                return response.body();
            } else {
                logger.error("Failed to fetch quiz assessment. Response code: {}, Error: {}",
                        response.code(), response.errorBody());
                throw new UserNotFoundException("Failed to fetch quiz assessment. Response code: " + response.code());
            }
        } catch (IOException e) {
            logger.error("API call failed due to network error: {}", e.getMessage());
            throw new UserNotFoundException("API call failed due to network error");
        }
    }

    public CatAssessmentResponseDto getCatAssessment(Integer courseId, Integer catId) {
        logger.info("Fetching CAT assessment for courseId={}, catId={}", courseId, catId);
        Call<CatAssessmentResponseDto> call = apiClient.getCatAssessment(courseId, catId);
        try {
            Response<CatAssessmentResponseDto> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                logger.info("Successfully fetched CAT assessment. Response code: {}", response.code());
                return response.body();
            } else {
                logger.error("Failed to fetch CAT assessment. Response code: {}, Error: {}",
                        response.code(), response.errorBody());
                throw new UserNotFoundException("Failed to fetch CAT assessment. Response code: " + response.code());
            }
        } catch (IOException e) {
            logger.error("API call failed due to network error: {}", e.getMessage());
            throw new UserNotFoundException("API call failed due to network error");
        }
    }



}