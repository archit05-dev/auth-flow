
package com.archit.authflow.controller;

import com.archit.authflow.dto.request.RegisterRequest;
import com.archit.authflow.dto.response.RegisterResponse;
import com.archit.authflow.service.AuthService;
import jakarta.validation.Valid;
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
}