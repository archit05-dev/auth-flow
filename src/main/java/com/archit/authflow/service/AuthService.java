
package com.archit.authflow.service;

import com.archit.authflow.dto.request.RegisterRequest;
import com.archit.authflow.dto.response.RegisterResponse;
import com.archit.authflow.entity.Otp;
import com.archit.authflow.entity.User;
import com.archit.authflow.repository.OtpRepository;
import com.archit.authflow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpRepository otpRepository;
    private final OtpService otpService;


    public AuthService(UserRepository userRepository,
                       OtpRepository otpRepository,
                       PasswordEncoder passwordEncoder,
                       OtpService otpService) {

        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
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

        // Temporary testing
        System.out.println("OTP for " + user.getEmail() + ": " + otp);

        return new RegisterResponse("OTP sent successfully");
    }
}