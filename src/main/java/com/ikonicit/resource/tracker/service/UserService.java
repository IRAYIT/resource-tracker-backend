package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.dto.ChangePasswordDTO;
import com.ikonicit.resource.tracker.dto.ForgotPasswordDTO;
import com.ikonicit.resource.tracker.dto.LoginDTO;
import com.ikonicit.resource.tracker.dto.ResourceDTO;

public interface UserService {

    public String changePassword(ChangePasswordDTO changePasswordDTO);

    public ResourceDTO login(LoginDTO loginDTO);

    public String forgotPassword(ForgotPasswordDTO forgotPasswordDTO);


}
