package com.gradeservice.retrofit;


import com.shared.dtos.SignatureDto;
import retrofit2.Call;
import retrofit2.http.*;


public interface AdminApiClient {
    @GET("/admin/get/signature")
    Call<SignatureDto> getSignature();
    @GET("/admin/get/vocallearnsignature")
    Call<SignatureDto> getVocalLearnSignature();
}
