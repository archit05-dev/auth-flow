
package com.archit.authflow.service;

import com.archit.authflow.dto.request.RegisterRequest;
import com.archit.authflow.dto.response.RegisterResponse;
import com.archit.authflow.entity.User;
import com.archit.authflow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        return new RegisterResponse("User registered successfully");
    }
}