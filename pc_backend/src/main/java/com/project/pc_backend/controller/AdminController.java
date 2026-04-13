package com.project.pc_backend.controller;

import com.project.pc_backend.dto.*;
import com.project.pc_backend.model.Donation;
import com.project.pc_backend.service.AppService;
import com.project.pc_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }

    @PostMapping("/add-education")
    public ApiResponse<?> addEducationTimeline(@RequestBody EducationTimelineRequest req){
        return appService.addEducationTimeline(req);
    }

    @DeleteMapping("/delete-education/{id}")
    public ApiResponse<?> deleteEducationTimeline(@PathVariable Long id){
        return appService.deleteEducationTimeline(id);
    }

    @PostMapping("/add-job")
    public ApiResponse<?> addJobTimeline(@RequestBody JobTimelineRequest req){
        return appService.addJobTimeline(req);
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
            @ModelAttribute CreateEventRequest req
    ) {
        ApiResponse<?> response = appService.createEvent(req);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete-event/{id}")
    public ApiResponse<?> deleteEvent(@PathVariable Long id){
        return appService.deleteEvent(id);
    }

}
