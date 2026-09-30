package com.archit.authflow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final RestClient restClient;

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${mail.from}")
    private String fromEmail;

    public EmailService(RestClient restClient) {
        this.restClient = restClient;
    }

    public void sendOtp(String to, String otp) {

        Map<String, Object> body = Map.of(
                "sender", Map.of("email", fromEmail),

                "to", List.of(
                        Map.of("email", to)
                ),

                "subject", "Your AuthFlow OTP",
                "textContent",
                """
                Hello %s,

                Your AuthFlow verification code is: %s

                This OTP expires in 5 minutes.

                If you didn't request this, ignore this email.

                - AuthFlow
                """.formatted(to, otp)
        );


        restClient.post()
                .uri("https://api.brevo.com/v3/smtp/email")
                .contentType(MediaType.APPLICATION_JSON)
                .header("api-key", apiKey)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}