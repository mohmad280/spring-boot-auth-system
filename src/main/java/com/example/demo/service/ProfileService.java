package com.example.demo.service;

import com.example.demo.io.ProfileRequest;
import com.example.demo.io.ProfileResponse;

public interface ProfileService {

    ProfileResponse createdProfile(ProfileRequest request);

    ProfileResponse getProfile(String email);

    void sendRestOtp(String email);

    void restPassword(String email, String otp, String newPassword);

    void sendOtp(String email);

    void verifyOtp(String email, String otp);

    String getLoggedInUserId(String email);
}
