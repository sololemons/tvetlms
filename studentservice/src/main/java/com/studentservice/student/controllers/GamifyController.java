package com.studentservice.student.controllers;

import com.studentservice.student.dtos.GamifyPointsDto;
import com.studentservice.student.entities.GamifyBadges;
import com.studentservice.student.entities.GamifyData;
import com.studentservice.student.entities.GamifyDataProfile;
import com.studentservice.student.services.GamifyServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/student/gamify")
@RequiredArgsConstructor
public class GamifyController {
    private final GamifyServices gamifyServices;
    @PostMapping("/update/points")
    private String  updateGamifyPoints(@RequestBody GamifyPointsDto gamifyPointsDto){
        return gamifyServices.updateGamifyPoints(gamifyPointsDto);
    }
    @GetMapping("/get/active/week")
    private GamifyData getActiveWeek(Principal principal){
        return gamifyServices.getActiveWeek(principal);
    }
    @GetMapping("/get/gamify/profile")
    public ResponseEntity<GamifyDataProfile> getGamifyProfile(Principal principal) {
        return ResponseEntity.ok(gamifyServices.getGamifyProfile(principal));
    }

}
