package com.project.pc_backend.controller;

import com.project.pc_backend.dto.*;
import com.project.pc_backend.model.Donation;
import com.project.pc_backend.model.EducationTimeline;
import com.project.pc_backend.service.AppService;
import com.project.pc_backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    private AppService appService;

    @Autowired
    private UserService userService;

    @PostMapping("/contact")
    public ApiResponse<?> contact(@RequestBody ContactDetails contactDetails){
        System.out.println("[Controller] /contact endpoint hit with body: " + contactDetails);
        return appService.contact(contactDetails);
    }

    @PostMapping("/donate")
    public ApiResponse<?> donate(@RequestBody DonationRequest donationRequest){
        return appService.initializeDonation(donationRequest);
    }

    @PostMapping("/verify-donation")
    public ApiResponse<?> verify(@RequestParam String reference, @RequestParam(required = false) String name){
        return appService.verifyPayment(reference,name);
    }

    @GetMapping("/event-history")
    public ApiResponse<?> getEventHistory(){
        return appService.getEventHistory();
    }

    @GetMapping("/education-history")
    public ApiResponse<?> getEducationHistory(){
        return appService.getEducationHistory();
    }

    @GetMapping("/job-history")
    public ApiResponse<?> getJobHistory(){
        return appService.getJobHistory();
    }
}
