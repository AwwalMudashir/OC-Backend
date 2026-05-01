package com.project.pc_backend.controller;

import com.project.pc_backend.dto.*;
import com.project.pc_backend.service.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AppController {

    private static final Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private AppService appService;

    @PostMapping("/contact")
    public ApiResponse<?> contact(@RequestBody @jakarta.validation.Valid ContactDetails contactDetails){
        logger.info("[Controller] /contact endpoint hit");
        return appService.contact(contactDetails);
    }

    @PostMapping("/donate")
    public ApiResponse<?> donate(@RequestBody DonationRequest donationRequest){
        return appService.initializeDonation(donationRequest);
    }

    @PutMapping("/event/{id}")
    public ApiResponse<?> updateEvent(@PathVariable Long id, @ModelAttribute CreateEventRequest req, @RequestHeader(value = "X-User", required = false) String doneBy) {
        return appService.updateEvent(id, req, doneBy);
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

    @PutMapping("/education/{id}")
    public ApiResponse<?> updateEducation(@PathVariable Long id, @RequestBody EducationTimelineRequest req, @RequestHeader(value = "X-User", required = false) String doneBy) {
        return appService.updateEducationTimeline(id, req, doneBy);
    }

    @GetMapping("/job-history")
    public ApiResponse<?> getJobHistory(){
        return appService.getJobHistory();
    }

    @PutMapping("/job/{id}")
    public ApiResponse<?> updateJob(@PathVariable Long id, @RequestBody JobTimelineRequest req, @RequestHeader(value = "X-User", required = false) String doneBy) {
        return appService.updateJobTimeline(id, req, doneBy);
    }
}
