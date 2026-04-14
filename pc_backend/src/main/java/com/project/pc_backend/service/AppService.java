package com.project.pc_backend.service;

import com.project.pc_backend.dto.*;
import com.project.pc_backend.model.Donation;
import com.project.pc_backend.model.EducationTimeline;
import com.project.pc_backend.model.Event;
import com.project.pc_backend.model.JobTimeline;
import com.project.pc_backend.repository.DonationRepository;
import com.project.pc_backend.repository.EducationTimelineRepo;
import com.project.pc_backend.repository.EventRepository;
import com.project.pc_backend.repository.JobTimelineRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AppService {

    private static final int MAX_EVENT_IMAGES = 10;
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".webp", ".gif"
    );

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

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private EducationTimelineRepo educationTimelineRepo;

    @Autowired
    private JobTimelineRepo jobTimelineRepo;

    @Autowired
    private EventRepository eventRepo;

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
            headers.setBearerAuth(secret_key);

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

    public ApiResponse<?> getEducationHistory() {
        try{
            List<EducationTimeline> history = educationTimelineRepo.findAll();
            return ApiResponse.success(200,history,"Education History Response");
        } catch (Exception e){
            return ApiResponse.error("An Error has Occured !",400);
        }
    }

    public ApiResponse<?> addEducationTimeline(EducationTimelineRequest req) {
        try{
            EducationTimeline obj = new EducationTimeline();
            obj.setQualification(req.getQualification());
            obj.setPeriod(req.getStartYear() + " - " + req.getEndYear());
            obj.setTitle(req.getTitle());

            return ApiResponse.success(200,educationTimelineRepo.save(obj),"Added EducationTimeline Successfully");
        } catch (Exception e){
            return ApiResponse.error("An Error has Occured !",400);
        }
    }

    public ApiResponse<?> addJobTimeline(JobTimelineRequest req) {
        try{
            JobTimeline obj = new JobTimeline();
            obj.setDesc(req.getDesc());
            obj.setTitle(req.getTitle());

            return ApiResponse.success(200,jobTimelineRepo.save(obj),"Added Job Timeline successfully !");
        } catch (Exception e){
            return ApiResponse.error("An Error has Occured !",400);
        }
    }

    public ApiResponse<?> getJobHistory() {
        try{
            List<JobTimeline> history = jobTimelineRepo.findAll();
            return ApiResponse.success(200,history,"Education History Response");
        } catch (Exception e){
            return ApiResponse.error("An Error has Occurred !",400);
        }
    }

    public ApiResponse<Double> getTotalDonations() {
        Double total = donationRepository.getTotalDonations();
        return ApiResponse.success(200,total, "Total donations fetched");
    }

    public ApiResponse<List<Donation>> getRecentDonations() {
        Pageable pageable = PageRequest.of(0, 5); // first 5
        Page<Donation> page = donationRepository
                .findByStatusOrderByCreatedAtDesc(DonationStatus.SUCCESS, pageable);

        return ApiResponse.success(200,page.getContent(), "Recent donations fetched");
    }

    public ApiResponse<?> deleteEducationTimeline(Long id) {
        try{
            educationTimelineRepo.deleteById(id);
            return ApiResponse.success(200,"Education Timeline deleted successfully","Deleted Education Timeline successfully !");
        } catch (Exception e){
            return ApiResponse.error("An Error has Occured !",400);
        }
    }

    public ApiResponse<?> deleteJobTimeline(Long id) {
        try{
            jobTimelineRepo.deleteById(id);
            return ApiResponse.success(200,"Job Timeline deleted successfully","Deleted Job Timeline successfully !");
        } catch (Exception e){
            return ApiResponse.error("An Error has Occured !",400);
        }
    }

    public ApiResponse<?> createEvent(CreateEventRequest req) {
        List<Path> storedFiles = new ArrayList<>();

        try{
            validateEventImages(req.getImages());

            Path uploadPath = Paths.get(uploadDir, "events").toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            List<String> imageUrls = new ArrayList<>();

            for (MultipartFile image : req.getImages()) {
                String extension = getValidatedExtension(image);
                String fileName = UUID.randomUUID() + extension;
                Path filePath = uploadPath.resolve(fileName);

                try (var inputStream = image.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                storedFiles.add(filePath);
                imageUrls.add("/uploads/events/" + fileName);
            }

            Event obj = new Event();
            obj.setDescription(req.getDescription());
            obj.setTitle(req.getTitle());
            obj.setLocation(req.getLocation());
            obj.setEventDate(req.getEventDate());
            obj.setImageUrls(imageUrls);
            obj.setVideoLink(req.getVideoLink());

            return ApiResponse.created(eventRepo.save(obj),"Event created successfully");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        } catch (IOException e){
            deleteStoredFiles(storedFiles);
            return ApiResponse.error("Unable to store event images right now. Please try again.",HttpStatus.INTERNAL_SERVER_ERROR.value());
        } catch (Exception e){
            deleteStoredFiles(storedFiles);
            return ApiResponse.error("Unable to create event right now. Please try again.",HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void validateEventImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("At least one event image is required.");
        }

        if (images.size() > MAX_EVENT_IMAGES) {
            throw new IllegalArgumentException("You can upload at most 10 images for a single event.");
        }

        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) {
                throw new IllegalArgumentException("Each uploaded image must be a non-empty file.");
            }

            getValidatedExtension(image);
        }
    }

    private String getValidatedExtension(MultipartFile image) {
        String originalFilename = image.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Each image must have a valid file name.");
        }

        int extensionStart = originalFilename.lastIndexOf('.');
        if (extensionStart < 0) {
            throw new IllegalArgumentException("Only image files with jpg, jpeg, png, webp, or gif extensions are allowed.");
        }

        String extension = originalFilename.substring(extensionStart).toLowerCase();
        String contentType = image.getContentType();

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)
                || (contentType != null && !contentType.toLowerCase().startsWith("image/"))) {
            throw new IllegalArgumentException("Only image files with jpg, jpeg, png, webp, or gif extensions are allowed.");
        }

        return extension;
    }

    private void deleteStoredFiles(List<Path> storedFiles) {
        for (Path storedFile : storedFiles) {
            try {
                Files.deleteIfExists(storedFile);
            } catch (IOException ignored) {
            }
        }
    }

    public ApiResponse<?> deleteEvent(Long id) {
        try{
            Event event = eventRepo.findById(id).orElse(null);

            if (event == null) {
                return ApiResponse.error("Event not found.", HttpStatus.NOT_FOUND.value());
            }

            deleteEventImages(event);
            eventRepo.delete(event);

            return ApiResponse.success(200,"Event deleted successfully","Deleted Event successfully !");
        } catch (IOException e){
            return ApiResponse.error("Unable to delete the event images right now. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR.value());
        } catch (Exception e){
            return ApiResponse.error("Unable to delete event right now. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void deleteEventImages(Event event) throws IOException {
        if (event.getImageUrls() == null || event.getImageUrls().isEmpty()) {
            return;
        }

        Path uploadsRoot = Paths.get(uploadDir).toAbsolutePath().normalize();

        for (String imageUrl : event.getImageUrls()) {
            if (imageUrl == null || imageUrl.isBlank()) {
                continue;
            }

            String relativePath = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;
            if (!relativePath.startsWith("uploads/")) {
                continue;
            }

            String pathInsideUploads = relativePath.substring("uploads/".length());
            Path filePath = uploadsRoot.resolve(pathInsideUploads).normalize();

            if (!filePath.startsWith(uploadsRoot)) {
                continue;
            }

            Files.deleteIfExists(filePath);
        }
    }

    public ApiResponse<?> getEventHistory() {
        try{
            List<Event> history = eventRepo.findAll();
            return ApiResponse.success(200,history,"Event History Response");
        } catch (Exception e){
            return ApiResponse.error("An Error has Occurred !",400);
        }
    }
}
