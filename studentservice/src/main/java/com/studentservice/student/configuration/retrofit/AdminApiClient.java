package com.studentservice.student.configuration.retrofit;


import retrofit2.Call;
import retrofit2.http.*;
import com.shared.dtos.ModuleDto;

import java.util.List;

public interface AdminApiClient {
    @GET("/course/get/module/{courseId}")
    Call<List<ModuleDto>> getModulesAssociatedWithCourse(@Path("courseId") Integer courseId);

}
