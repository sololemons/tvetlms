package com.studentservice.student.configuration.retrofit;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.shared.dtos.ModuleDto;

import java.util.List;


@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class RetrofitController {
    private final RetrofitService retrofitService;
    @GetMapping("/courses/{courseId}/modules")
    public List<ModuleDto> getModulesForCourse(@PathVariable Integer courseId) {
        return retrofitService.getModules(courseId);
    }



}
