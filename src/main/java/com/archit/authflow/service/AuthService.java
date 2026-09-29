
package com.archit.authflow.service;

import com.archit.authflow.dto.request.*;
import com.archit.authflow.dto.response.AuthResponse;
import com.archit.authflow.dto.response.MessageResponse;
import com.archit.authflow.dto.response.RefreshTokenResponse;
import com.archit.authflow.dto.response.RegisterResponse;
import com.archit.authflow.entity.Otp;
import com.archit.authflow.entity.RefreshToken;
import com.archit.authflow.entity.User;
import com.archit.authflow.exception.*;
import com.archit.authflow.repository.OtpRepository;
import com.archit.authflow.repository.RefreshTokenRepository;
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
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;



    public AuthService(
            UserRepository userRepository,
            OtpRepository otpRepository,
            PasswordEncoder passwordEncoder,
            OtpService otpService,
            EmailService emailService,
            JwtService jwtService,
            RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
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

        otpRepository.save(otpEntity);

        emailService.sendOtp(user.getEmail(), otp);

        return new RegisterResponse("OTP sent successfully");
    }


    @Transactional
    public MessageResponse verifyOtp(VerifyOtpRequest request) {

        Otp otpEntity = otpRepository
                .findTopByEmailOrderByIdDesc(request.email())
                .orElseThrow(() ->
                        new InvalidOtpException("OTP not found"));

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));


        if (otpEntity.getExpiry().isBefore(LocalDateTime.now())) {

            otpRepository.delete(otpEntity);

            throw new InvalidOtpException("OTP expired");
        }

        if (!passwordEncoder.matches(request.otp(), otpEntity.getOtpHash())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        user.setVerified(true);
        userRepository.save(user);

        otpRepository.delete(otpEntity);

        return new MessageResponse("Email verified successfully");
    }

    @Transactional
    public MessageResponse resendOtp(ResendOtpRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

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

            resendCount = latestOtp.getResendCount() + 1;

            otpRepository.delete(latestOtp);
        }

        String otp = otpService.generateOtp();

        Otp newOtp = new Otp();

        newOtp.setEmail(user.getEmail());
        newOtp.setOtpHash(otpService.hashOtp(otp));
        newOtp.setExpiry(otpService.getExpiryTime());
        newOtp.setResendCount(resendCount);

        otpRepository.save(newOtp);

        emailService.sendOtp(user.getEmail(), otp);

        return new MessageResponse("OTP resent successfully.");
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!user.isVerified()) {
            throw new EmailNotVerifiedException("Please verify your email first");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);
        refreshTokenRepository.flush();

        RefreshToken refreshTokenEntity = new RefreshToken();

        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setExpiry(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(accessToken, refreshToken);
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken storedToken = refreshTokenRepository
                .findByToken(request.refreshToken())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid refresh token"));

        if (storedToken.getExpiry().isBefore(LocalDateTime.now())) {

            refreshTokenRepository.delete(storedToken);

            throw new InvalidCredentialsException("Refresh token expired");
        }

        User user = storedToken.getUser();

        if (!jwtService.isTokenValid(request.refreshToken(), user)) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        return new RefreshTokenResponse(newAccessToken);
    }

    @Transactional
    public MessageResponse logout(User user) {

        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        return new MessageResponse("Logged out successfully");
    }
}