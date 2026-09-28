package com.archit.authflow.dto.response;

public record AuthResponse(

        String accessToken,
        String refreshToken

) {}