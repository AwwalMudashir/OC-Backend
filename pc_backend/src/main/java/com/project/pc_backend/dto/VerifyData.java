package com.project.pc_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyData {
    private String status;
    private String reference;
    private int amount;
    private Customer customer;

    @Data
    public static class Customer {
        private String email;
    }
}

