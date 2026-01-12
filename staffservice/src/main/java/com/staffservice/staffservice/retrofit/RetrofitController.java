package com.staffservice.staffservice.retrofit;

import com.shared.dtos.ModuleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class RetrofitController {
    private final RetrofitService retrofitService;




}
