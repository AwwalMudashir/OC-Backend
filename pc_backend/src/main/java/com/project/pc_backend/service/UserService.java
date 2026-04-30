package com.project.pc_backend.service;

import com.project.pc_backend.dto.LoginDto;
import com.project.pc_backend.dto.LoginResponse;
import com.project.pc_backend.dto.UserDto;
import com.project.pc_backend.model.User;
import com.project.pc_backend.repository.UserRepository;
import com.project.pc_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepo;

    @Value("${app.email}")
    private String app_email;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtService;

    private final AuthenticationManager authenticationManager;

    private final EmailTemplateService emailTemplateService;

    private final ResendEmailService resendEmailService;

    public UserService(
            UserRepository userRepo,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtService,
            AuthenticationManager authenticationManager,
            EmailTemplateService emailService, ResendEmailService resendEmailService
    ) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailTemplateService = emailService;
        this.resendEmailService = resendEmailService;
    }

    public ResponseEntity<?> register(UserDto userDto) {
        return register(userDto, userDto.getUsername());
    }

    public ResponseEntity<?> register(UserDto userDto, String createdBy) {
        String email = userDto.getEmail() == null ? null : userDto.getEmail().trim().toLowerCase();

        if (email == null || email.isBlank()) {
            return new ResponseEntity<>("Email is required", HttpStatus.BAD_REQUEST);
        }

        if (userRepo.existsByEmail(email)){
            return new ResponseEntity<>("Email Already Exists", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setDoneBy(resolveCreatorName(createdBy));

        try {
            String html = emailTemplateService.adminWelcomeTemplate(user.getUsername());
            logger.info("[Email] Admin welcome email send start: to={}", user.getEmail());

            resendEmailService.sendEmail(
                    user.getEmail(),
                    "Welcome to The Oroye Campaign \" + user.getUsername() + \",\\nYou have Admin Capabilities now !, You can add jobs,education timelines,events and so on dynamically. Just log in to the Admin Dashboard by adding this (/admin) to the base of the website. This prompts you to login, enter your credentials and start working.\\nRemember to not share your credentials with any other personnel who doesn't need this access as this will be violating The Oroye Campaign's Data and legal actions can be taken against you.\\n\\nWelcome Once Again !!,\\nThe Oroye Campaign",
                    html
            );

            logger.info("[Email] Admin welcome email send complete");
            return new ResponseEntity<>(userRepo.save(user), HttpStatus.OK);
        } catch (DataIntegrityViolationException dive) {
            // possibly another request inserted same email concurrently
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }
    }

    private String resolveCreatorName(String creatorIdentity) {
        if (creatorIdentity == null || creatorIdentity.isBlank()) {
            return null;
        }

        if (creatorIdentity.contains("@")) {
            User creator = userRepo.findByEmail(creatorIdentity);
            return creator != null && creator.getUsername() != null ? creator.getUsername() : creatorIdentity;
        }

        return creatorIdentity;
    }

    public ResponseEntity<?> login(LoginDto request){
        String email = request.getEmail() == null ? null : request.getEmail().trim().toLowerCase();

        if (email == null || email.isBlank() || request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email and password are required");
        }

        User user = userRepo.findByEmail(email);

        if (user == null){
            return new ResponseEntity<>("User doesn't exist",HttpStatus.BAD_REQUEST);
        }

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String jwt = jwtService.generateToken(email);

        LoginResponse response = new LoginResponse();
        response.setToken(jwt);
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());

        return ResponseEntity.ok(response);
    }


}
