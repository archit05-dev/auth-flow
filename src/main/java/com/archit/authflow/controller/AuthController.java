
package com.archit.authflow.controller;

import com.archit.authflow.dto.request.*;
import com.archit.authflow.dto.response.AuthResponse;
import com.archit.authflow.dto.response.MessageResponse;
import com.archit.authflow.dto.response.RefreshTokenResponse;
import com.archit.authflow.dto.response.RegisterResponse;
import com.archit.authflow.entity.User;
import com.archit.authflow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/verify-otp")
    public MessageResponse verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        return authService.verifyOtp(request);
    }

    @PostMapping("/resend-otp")
    public MessageResponse resendOtp(
            @Valid @RequestBody ResendOtpRequest request) {

        return authService.resendOtp(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        MessageResponse response = authService.logout(user);

        return ResponseEntity.ok(response);
    }
}