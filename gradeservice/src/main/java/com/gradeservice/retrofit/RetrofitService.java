package com.gradeservice.retrofit;

import com.gradeservice.exceptions.UserNotFoundException;
import com.shared.dtos.SignatureDto;
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


    public SignatureDto getSignature() {
        Call<SignatureDto> call = apiClient.getSignature();
        try {
            Response<SignatureDto> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                logger.info("Successfully fetched The signature. Response code: {}",
                         response.code());
                return response.body();
            } else {
                logger.error("Failed to fetch signature. Response code: {}, Error: {}",
                        response.code(), response.errorBody());
                throw new UserNotFoundException("Failed to fetch modules. Response code: " + response.code());
            }
        } catch (IOException e) {
            logger.error("API call failed due to network error: {}", e.getMessage());
            throw new UserNotFoundException("API call failed due to network error");
        }
    }



}