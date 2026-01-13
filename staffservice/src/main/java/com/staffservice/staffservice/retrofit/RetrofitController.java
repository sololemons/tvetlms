package com.staffservice.staffservice.retrofit;

import com.shared.dtos.CatAssessmentResponseDto;
import com.shared.dtos.QuizAssessmentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class RetrofitController {
    private final RetrofitService retrofitService;
 @GetMapping("/course/get/quizAssessment")
 private ResponseEntity<QuizAssessmentResponseDto> getQuizAssessment
         (@RequestParam Integer courseId,@RequestParam Integer moduleId, @RequestParam Integer quizId) {
     return ResponseEntity.ok(retrofitService.getQuizAssessment(courseId,moduleId,quizId));

 }
    @GetMapping("/course/get/catAssessment")
    private ResponseEntity<CatAssessmentResponseDto> getCatAssessment
            (@RequestParam Integer courseId, @RequestParam Integer catId) {
        return ResponseEntity.ok(retrofitService.getCatAssessment(courseId,catId));

    }




}
