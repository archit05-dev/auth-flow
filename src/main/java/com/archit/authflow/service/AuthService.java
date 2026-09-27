
package com.archit.authflow.service;

import com.archit.authflow.dto.request.RegisterRequest;
import com.archit.authflow.dto.request.VerifyOtpRequest;
import com.archit.authflow.dto.response.MessageResponse;
import com.archit.authflow.dto.response.RegisterResponse;
import com.archit.authflow.entity.Otp;
import com.archit.authflow.entity.User;
import com.archit.authflow.repository.OtpRepository;
import com.archit.authflow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpRepository otpRepository;
    private final OtpService otpService;
    private final EmailService emailService;



    public AuthService(
            UserRepository userRepository,
            OtpRepository otpRepository,
            PasswordEncoder passwordEncoder,
            OtpService otpService,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.emailService = emailService;
    }




    public RegisterResponse register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.email())){
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setVerified(false);

        userRepository.save(user);


        String otp = otpService.generateOtp();

        Otp otpEntity = new Otp();
        otpEntity.setEmail(user.getEmail());
        otpEntity.setOtpHash(otpService.hashOtp(otp));
        otpEntity.setExpiry(otpService.getExpiryTime());
        otpEntity.setResendCount(0);
        otpEntity.setUsed(false);

        otpRepository.save(otpEntity);

        emailService.sendOtp(user.getEmail(), otp);

        return new RegisterResponse("OTP sent successfully");
    }


    @Transactional
    public MessageResponse verifyOtp(VerifyOtpRequest request) {

        Otp otpEntity = otpRepository
                .findTopByEmailOrderByExpiryDesc(request.email())
                .orElseThrow(() ->
                        new RuntimeException("OTP not found"));

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (otpEntity.isUsed()) {
            throw new RuntimeException("OTP already used");
        }

        if (otpEntity.getExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!passwordEncoder.matches(request.otp(), otpEntity.getOtpHash())) {
            throw new RuntimeException("Invalid OTP");
        }

        otpEntity.setUsed(true);
        user.setVerified(true);

        return new MessageResponse("Email verified successfully");
    }
}