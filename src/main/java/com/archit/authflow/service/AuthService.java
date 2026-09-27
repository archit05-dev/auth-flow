
package com.archit.authflow.service;

import com.archit.authflow.dto.request.RegisterRequest;
import com.archit.authflow.dto.request.ResendOtpRequest;
import com.archit.authflow.dto.request.VerifyOtpRequest;
import com.archit.authflow.dto.response.MessageResponse;
import com.archit.authflow.dto.response.RegisterResponse;
import com.archit.authflow.entity.Otp;
import com.archit.authflow.entity.User;
import com.archit.authflow.exception.AlreadyVerifiedException;
import com.archit.authflow.exception.InvalidOtpException;
import com.archit.authflow.exception.TooManyResendAttemptsException;
import com.archit.authflow.exception.UserAlreadyExistsException;
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
            throw new UserAlreadyExistsException("Email already exists");
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
                .findTopByEmailOrderByIdDesc(request.email())
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
            throw new InvalidOtpException("Invalid OTP");
        }

        otpEntity.setUsed(true);
        user.setVerified(true);

        return new MessageResponse("Email verified successfully");
    }

    @Transactional
    public MessageResponse resendOtp(ResendOtpRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (user.isVerified()) {
            throw new AlreadyVerifiedException(
                    "Email is already verified.");
        }

        Otp latestOtp = otpRepository
                .findTopByEmailOrderByIdDesc(request.email())
                .orElse(null);

        int resendCount = 0;

        if (latestOtp != null) {

            if (latestOtp.getResendCount() >= 3) {
                throw new TooManyResendAttemptsException(
                        "Maximum OTP resend attempts reached.");
            }

            latestOtp.setUsed(true);

            resendCount = latestOtp.getResendCount() + 1;
        }

        String otp = otpService.generateOtp();

        Otp newOtp = new Otp();

        newOtp.setEmail(user.getEmail());
        newOtp.setOtpHash(otpService.hashOtp(otp));
        newOtp.setExpiry(otpService.getExpiryTime());
        newOtp.setUsed(false);
        newOtp.setResendCount(resendCount);

        otpRepository.save(newOtp);

        emailService.sendOtp(user.getEmail(), otp);

        return new MessageResponse("OTP resent successfully.");
    }
}