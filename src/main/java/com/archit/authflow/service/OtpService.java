
package com.archit.authflow.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final PasswordEncoder passwordEncoder;

    public OtpService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String generateOtp() {
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    public String hashOtp(String otp) {
        return passwordEncoder.encode(otp);
    }

    public LocalDateTime getExpiryTime() {
        return LocalDateTime.now().plusMinutes(5);
    }
}