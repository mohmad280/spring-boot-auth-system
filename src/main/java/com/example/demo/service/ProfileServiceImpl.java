package com.example.demo.service;

import com.example.demo.entity.UserEntity;
import com.example.demo.io.ProfileRequest;
import com.example.demo.io.ProfileResponse;
import com.example.demo.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public ProfileResponse createdProfile(ProfileRequest request) {
        UserEntity newProfile = convertToUserEntity(request);
        if(!userRepo.existsByEmail(request.getEmail())) {
            newProfile = userRepo.save(newProfile);
            return convertToProfileResponse(newProfile);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exist");
    }

    @Override
    public ProfileResponse getProfile(String email) {
       UserEntity existingUser =  userRepo.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found: " + email ));
       return convertToProfileResponse(existingUser);
    }

    @Override
    public void sendRestOtp(String email) {
        UserEntity existingEntity = userRepo.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found: " + email));

        //Generate 6 digit otp
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        // calculate expiry time(current time + 15 minutes in milliseconds)
        long expiryTime = System.currentTimeMillis() + (15 * 60 * 1000);

        //update profile user
        existingEntity.setRestOtp(otp);
        existingEntity.setResetOtpExpireAt(expiryTime);

        //save into database
        userRepo.save(existingEntity);

        try {
            emailService.sendRestOtpEmail(existingEntity.getEmail(), otp);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to send email");
        }
    }

    @Override
    public void restPassword(String email, String otp, String newPassword) {
        UserEntity existingUser = userRepo.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found: " +email));

        if(existingUser.getRestOtp() == null || !existingUser.getRestOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if(existingUser.getResetOtpExpireAt() < System.currentTimeMillis()) {
            throw new RuntimeException("OTP Expired");
        }

        existingUser.setPassword(passwordEncoder.encode(newPassword));
        existingUser.setRestOtp(null);
        existingUser.setResetOtpExpireAt(0L);

        userRepo.save(existingUser);
    }

    @Override
    public void sendOtp(String email) {
        UserEntity existingUser = userRepo.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found: " +email));

        if(existingUser.getIsAccountVerified() != null && existingUser.getIsAccountVerified()) {
            return;
        }

        // Generate 6 digit otp
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        // calculate expiry time(current time + 24 hours in milliseconds)
        long expiryTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

        //Update the user entity
        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpireAt(expiryTime);

        // save to database
        userRepo.save(existingUser);

        try {
            emailService.sendOtpEmail(existingUser.getEmail(), otp);
        } catch (Exception e) {
            throw new RuntimeException("Unable to send email");
        }
    }

    @Override
    public void verifyOtp(String email, String otp) {
        UserEntity existingUser = userRepo.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found: " + email));
        if(existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if(existingUser.getVerifyOtpExpireAt() < System.currentTimeMillis()) {
            throw new RuntimeException("OTP Expired");
        }

        existingUser.setIsAccountVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpireAt(0L);

        userRepo.save(existingUser);
    }

    @Override
    public String getLoggedInUserId(String email) {
        UserEntity existingUser = userRepo.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found: " +email));
        return existingUser.getUserId();
    }

    private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
        return ProfileResponse.builder()
                .name(newProfile.getName())
                .email(newProfile.getEmail())
                .userId(newProfile.getUserId())
                .isAccountVerified(newProfile.getIsAccountVerified())
                .build();
    }

    private UserEntity convertToUserEntity(ProfileRequest request) {
        return UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .restOtp(null)
                .build();
    }
}
