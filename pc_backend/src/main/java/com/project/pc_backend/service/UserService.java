package com.project.pc_backend.service;

import com.project.pc_backend.dto.EmailDetails;
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

@Service
public class UserService {

    private final UserRepository userRepo;

    @Value("${app.email}")
    private String app_email;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtService;

    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;

    public UserService(
            UserRepository userRepo,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtService,
            AuthenticationManager authenticationManager,
            EmailService emailService
    ) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    public ResponseEntity<?> register(UserDto userDto) {
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

        try {
            EmailDetails emailDetails = new EmailDetails();
            emailDetails.setSender(app_email);
            emailDetails.setSubject("Welcome Admin");
            emailDetails.setRecipient(user.getEmail());
            emailDetails.setMessageBody("Welcome to The Oroye Campaign " + user.getUsername() + ",\nYou have Admin Capabilities now !, You can add jobs,education timelines,events and so on dynamically. Just log in to the Admin Dashboard by adding this (/admin) to the base of the website. This prompts you to login, enter your credentials and start working.\nRemember to not share your credentials with any other personnel who doesn't need this access as this will be violating The Oroye Campaign's Data and legal actions can be taken against you.\n\nWelcome Once Again !!,\nThe Oroye Campaign");

            emailService.sendMail(emailDetails);
            return new ResponseEntity<>(userRepo.save(user), HttpStatus.OK);
        } catch (DataIntegrityViolationException dive) {
            // possibly another request inserted same email concurrently
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }
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
