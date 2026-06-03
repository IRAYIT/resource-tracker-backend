package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.*;

public interface UserService {

    public String changePassword(ChangePasswordDTO changePasswordDTO);

    public ResourceDTO login(LoginDTO loginDTO);

    public String forgotPassword(ForgotPasswordDTO forgotPasswordDTO);


    Boolean checkEmail(String email);

    String sendOtp(String email);

    String verifyOtp(String email, String otp);

    String setNewPassword(NewPasswordDTO dto);
}
