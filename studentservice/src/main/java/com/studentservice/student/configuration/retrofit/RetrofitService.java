package com.studentservice.student.configuration.retrofit;

import com.placebet.placebetservice.dtos.Transaction;
import com.placebet.placebetservice.exceptions.UserNotFoundException;
import com.placebet.placebetservice.retrofit.models.ValidateResponse;
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


    public ValidateResponse getValidateResponse(String authHeader) {
        Call<ValidateResponse> call = apiClient.getValidateResponse(authHeader);
        try {
            Response<ValidateResponse> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                logger.info("Successfully fetched {} validateResponse. Response: {}, Response code: {}",
                        response.body(), response.body(), response.code());
                return response.body();
            } else {
                logger.error("Failed to fetch validateResponse. Response code: {}, Error: {}",
                        response.code(), response.errorBody());
                throw new UserNotFoundException("Failed to fetch transactions. Response code: " + response.code());
            }
        } catch (IOException e) {
            logger.error("API call failed due to network error: {}", e.getMessage());
            throw new UserNotFoundException("API call failed due to network error");
        }
    }

    public Transaction updateBalance(Transaction transaction) {
        Call<Transaction> call = apiClient.updateBalance(transaction);
        try {
            Response<Transaction> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                logger.info("Successfully sent the transaction dto: {} Response Code: {}",
                        response.body(), response.code());
                return response.body();
            } else {
                logger.error("Failed to update balance and send the transaction dto   Response code: {} , Error: {}",
                        response.code(), response.errorBody());
                throw new UserNotFoundException("Failed to send transaction dto . Response code: " + response.code());

            }
        } catch (IOException e) {
            logger.error("API call failed due to network error: {}", e.getMessage());
            throw new UserNotFoundException("API call failed due to network error");

        }
    }


}