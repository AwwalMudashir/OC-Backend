package com.project.pc_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaystackVerifyResponse {
    private boolean status;
    private String message;
    private VerifyData data;
}
