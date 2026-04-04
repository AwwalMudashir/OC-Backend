package com.project.pc_backend.service;

import com.project.pc_backend.dto.*;
import com.project.pc_backend.model.Donation;
import com.project.pc_backend.repository.DonationRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class AppService {

    @Value("${app.email}")
    private String app_email;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${callback_url}")
    private String callback_url;

    @Value("${secret_key}")
    private String secret_key;

    @Autowired
    private DonationRepository donationRepository;

    public ApiResponse<?> contact(ContactDetails contactDetails) {
        try{
            EmailDetails emailDetails = new EmailDetails();

            emailDetails.setRecipient(app_email);
            emailDetails.setSender(contactDetails.getEmail());
            emailDetails.setSubject("Contact Email From " + contactDetails.getName() + " - via Website");
            emailDetails.setMessageBody("The Below is a message");

            emailService.sendMail(emailDetails);

            return ApiResponse.success(200,"","Thanks for contacting us !");
        } catch (Exception e){
            return ApiResponse.error("An Error has Occured !",400);
        }

    }

    public ApiResponse<?> initializeDonation(DonationRequest donationRequest) {
        try{
            String url = "https://api.paystack.co/transaction/initialize";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(secret_key); // sk_test_xxx

            PayStackRequest payStackRequest = new PayStackRequest();
            payStackRequest.setEmail(donationRequest.getEmail());
            payStackRequest.setAmount(donationRequest.getAmount() * 100.0);
            payStackRequest.setCallback_url(callback_url);

            HttpEntity<PayStackRequest> entity = new HttpEntity<>(payStackRequest, headers);

            ResponseEntity<PayStackResponse> api_response =
                    restTemplate.postForEntity(url, entity, PayStackResponse.class);

            PayStackResponse api_body = api_response.getBody();

            String authorizationUrl = api_body.getData().getAuthorizationUrl();
            String reference = api_body.getData().getReference();

            DonationResponse donationResponse = new DonationResponse();
            donationResponse.setReference(reference);
            donationResponse.setAuthorizationUrl(authorizationUrl);

            return ApiResponse.success(200,donationResponse,"Donation Initialized");

        } catch (Exception e) {
            return ApiResponse.error("Error Initializing Donation",500);
        }

    }

    public ApiResponse<String> verifyPayment(String reference,String name) {

        if (donationRepository.existsByReference(reference)) {
            return ApiResponse.success(200, "Already Verified","Done");
        }

        String url = "https://api.paystack.co/transaction/verify/" + reference;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secret_key);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<PaystackVerifyResponse> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, PaystackVerifyResponse.class);

        PaystackVerifyResponse body = response.getBody();

        if (body != null && body.isStatus()) {

            String paymentStatus = body.getData().getStatus();

            Donation donation = new Donation();
            donation.setReference(body.getData().getReference());
            donation.setName(name);
            donation.setEmail(body.getData().getCustomer().getEmail());

            BigDecimal amount = BigDecimal.valueOf(body.getData().getAmount() / 100.0);
            donation.setAmount(amount);

            if ("success".equalsIgnoreCase(paymentStatus)) {
                donation.setStatus(DonationStatus.SUCCESS);
            } else if ("failed".equalsIgnoreCase(paymentStatus)) {
                donation.setStatus(DonationStatus.FAILED);
            } else {
                donation.setStatus(DonationStatus.PENDING);
            }

            donationRepository.save(donation);

            if (donation.getStatus() == DonationStatus.SUCCESS) {
                return ApiResponse.success(200,"Payment verified and saved", "Success");
            }
        }

        return ApiResponse.error("Payment verification failed", 400);
    }
}
