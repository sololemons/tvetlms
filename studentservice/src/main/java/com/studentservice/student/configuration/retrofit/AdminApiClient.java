package com.studentservice.student.configuration.retrofit;


import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AdminApiClient {

    @POST("/validate/user")
    Call <ValidateResponse> getValidateResponse(@Header("Authorization") String authHeader);

}
