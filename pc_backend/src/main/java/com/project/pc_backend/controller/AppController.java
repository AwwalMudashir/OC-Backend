package com.project.pc_backend.controller;

import com.project.pc_backend.dto.ApiResponse;
import com.project.pc_backend.dto.ContactDetails;
import com.project.pc_backend.dto.DonationRequest;
import com.project.pc_backend.service.AppService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    private AppService appService;

    @PostMapping("/contact")
    public ApiResponse<?> contact(@RequestBody ContactDetails contactDetails){
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


}
