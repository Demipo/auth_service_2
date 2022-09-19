package com.threeline.auth_service.service;

import com.threeline.auth_service.dto.*;
import com.threeline.auth_service.entity.ForgotPassword;
import com.threeline.auth_service.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
 * AuthService
 */
public interface AuthService {

  User signup(User user);
  void logout(HttpServletRequest request);
  LoginResponseDTO login(LoginRequestDTO user);
  void initiateForgotPassword(ForgotPasswordRequestDTO fg);
  ForgotPassword verifyForgotPasswordCode(VerifyForgotPasswordDTO vfp);
  void resetPassword(ResetPasswordDTO resetPasswordDTO);
  void changePassword(ChangePasswordDTO changePasswordDTO, String userEmail);

}
