package com.archit.authflow.service;

import com.archit.authflow.repository.OtpRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OtpCleanupService {

    private final OtpRepository otpRepository;

    public OtpCleanupService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Scheduled(fixedRate = 60000) // Every 60 seconds
    @Transactional
    public void deleteExpiredOtps() {

        otpRepository.deleteByExpiryBefore(LocalDateTime.now());
    }
}