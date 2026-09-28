package com.archit.authflow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;


    @Value("${mail.from}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String to, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Your AuthFlow OTP");
        message.setText("""
                Hello %s,
                
                Your AuthFlow verification code is: %s
                
                This OTP will expire in 5 minutes.
                
                If you didn't request this, you can safely ignore this email.
                
                - AuthFlow
                """.formatted(to, otp));

        mailSender.send(message);
    }
}