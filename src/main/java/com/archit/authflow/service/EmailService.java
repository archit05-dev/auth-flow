package com.archit.authflow.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String to, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Your AuthFlow OTP");
        message.setText(
                "Your OTP is: " + otp +
                        "\n\nIt expires in 5 minutes."
        );

        mailSender.send(message);
    }
}