package com.ikonicit.resource.tracker.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final Map<String, String> otpStore = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> otpExpiry = new ConcurrentHashMap<>();

    public String generateOtp(String email) {
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        otpStore.put(email, otp);
        otpExpiry.put(email, LocalDateTime.now().plusMinutes(15));
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        String storedOtp = otpStore.get(email);
        LocalDateTime expiry = otpExpiry.get(email);

        if (storedOtp != null && storedOtp.equals(otp) && expiry != null && LocalDateTime.now().isBefore(expiry)) {
            otpStore.remove(email);
            otpExpiry.remove(email);
            return true;
        }

        if (expiry != null && LocalDateTime.now().isAfter(expiry)) {
            otpStore.remove(email);
            otpExpiry.remove(email);
        }

        return false;
    }
}