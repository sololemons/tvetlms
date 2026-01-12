package com.staffservice.staffservice.retrofit;

import com.shared.dtos.ModuleDto;
import com.studentservice.student.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;


@Service
public class RetrofitService {
    private static final Logger logger = LoggerFactory.getLogger(RetrofitService.class);
    private final AdminApiClient apiClient;

    public RetrofitService(RetrofitClient retrofitClient) {
        this.apiClient = retrofitClient.getClient().create(AdminApiClient.class);
    }


    public List<ModuleDto> getModules(Integer courseId) {
        logger.info(courseId.toString());
        Call<List<ModuleDto>> call = apiClient.getModulesAssociatedWithCourse(courseId);
        try {
            Response<List<ModuleDto>> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                logger.info("Successfully fetched {} modules. Response code: {}",
                        response.body().size(), response.code());
                return response.body();
            } else {
                logger.error("Failed to fetch modules. Response code: {}, Error: {}",
                        response.code(), response.errorBody());
                throw new UserNotFoundException("Failed to fetch modules. Response code: " + response.code());
            }
        } catch (IOException e) {
            logger.error("API call failed due to network error: {}", e.getMessage());
            throw new UserNotFoundException("API call failed due to network error");
        }
    }



}