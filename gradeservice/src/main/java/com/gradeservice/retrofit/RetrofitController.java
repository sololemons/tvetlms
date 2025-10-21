package com.gradeservice.retrofit;

import com.shared.dtos.SignatureDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/grade")
@RequiredArgsConstructor
public class RetrofitController {
    private final RetrofitService retrofitService;

    @GetMapping("/get/signature")
    public SignatureDto getSignature() {
        return retrofitService.getSignature();
    }



}
