package com.project.pc_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactDetails {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Message is required")
    private String message;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
