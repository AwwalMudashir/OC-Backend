package com.project.pc_backend.controller;

import com.project.pc_backend.dto.*;
import com.project.pc_backend.model.Donation;
import com.project.pc_backend.service.AppService;
import com.project.pc_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AppService appService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto){
        return userService.register(userDto);
    }

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody UserDto userDto, Authentication authentication){
        String createdBy = authentication != null ? authentication.getName() : null;
        return userService.register(userDto, createdBy);
    }

    @GetMapping("/all-admins")
    public ApiResponse<?> allAdmins(@RequestBody UserDto userDto, Authentication authentication){
        String createdBy = authentication != null ? authentication.getName() : null;
        return appService.getAllAdmins(userDto, createdBy);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }

    @PostMapping("/add-education")
    public ApiResponse<?> addEducationTimeline(@RequestBody EducationTimelineRequest req, Authentication authentication){
        String doneBy = authentication != null ? authentication.getName() : null;
        return appService.addEducationTimeline(req, doneBy);
    }

    @DeleteMapping("/delete-education/{id}")
    public ApiResponse<?> deleteEducationTimeline(@PathVariable Long id){
        return appService.deleteEducationTimeline(id);
    }

    @PostMapping("/add-job")
    public ApiResponse<?> addJobTimeline(@RequestBody JobTimelineRequest req, Authentication authentication){
        String doneBy = authentication != null ? authentication.getName() : null;
        return appService.addJobTimeline(req, doneBy);
    }

    @DeleteMapping("/delete-job/{id}")
    public ApiResponse<?> deleteJobTimeline(@PathVariable Long id){
        return appService.deleteJobTimeline(id);
    }

    @GetMapping("/donations/total")
    public ResponseEntity<ApiResponse<Double>> getTotalDonations() {
        return ResponseEntity.ok(appService.getTotalDonations());
    }

    @GetMapping("/donations/recent")
    public ResponseEntity<ApiResponse<List<Donation>>> getRecentDonations() {
        return ResponseEntity.ok(appService.getRecentDonations());
    }

    @PostMapping(value = "/add-events", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> createEvent(
            @ModelAttribute CreateEventRequest req,
            Authentication authentication
    ) {
        String doneBy = authentication != null ? authentication.getName() : null;
        ApiResponse<?> response = appService.createEvent(req, doneBy);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete-event/{id}")
    public ApiResponse<?> deleteEvent(@PathVariable Long id){
        return appService.deleteEvent(id);
    }

    @GetMapping("/event/{id}")
    public ApiResponse<?> getEventById(@PathVariable Long id) {
        return appService.getEventById(id);
    }

    @GetMapping("/education/{id}")
    public ApiResponse<?> getEducationById(@PathVariable Long id) {
        return appService.getEducationTimelineBy(id);
    }

    @GetMapping("/job/{id}")
    public ApiResponse<?> getJobById(@PathVariable Long id) {
        return appService.getJobTimelineBy(id);
    }

    @PutMapping("/event/{id}")
    public ApiResponse<?> updateEvent(@PathVariable Long id, @ModelAttribute CreateEventRequest req, @RequestHeader(value = "X-User", required = false) String doneBy) {
        return appService.updateEvent(id, req, doneBy);
    }

    @PutMapping("/education/{id}")
    public ApiResponse<?> updateEducation(@PathVariable Long id, @RequestBody EducationTimelineRequest req, @RequestHeader(value = "X-User", required = false) String doneBy) {
        return appService.updateEducationTimeline(id, req, doneBy);
    }

    @PutMapping("/job/{id}")
    public ApiResponse<?> updateJob(@PathVariable Long id, @RequestBody JobTimelineRequest req, @RequestHeader(value = "X-User", required = false) String doneBy) {
        return appService.updateJobTimeline(id, req, doneBy);
    }
}
