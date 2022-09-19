package com.threeline.auth_service.service;


import com.threeline.auth_service.dto.*;
import com.threeline.auth_service.entity.ForgotPassword;
import com.threeline.auth_service.entity.TokenBlacklist;
import com.threeline.auth_service.entity.User;
import com.threeline.auth_service.exceptions.CustomException;
import com.threeline.auth_service.repository.ForgotPasswordRepository;
import com.threeline.auth_service.repository.TokenBlacklistRepository;
import com.threeline.auth_service.repository.UserRepository;
import com.threeline.auth_service.security.JwtTokenProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * UserService
 */
@Service
public class AuthServiceImpl implements AuthService {

  private UserRepository userRepository;
  private ForgotPasswordRepository forgotPasswordRepository;
  private PasswordEncoder passwordEncoder;
  private JwtTokenProvider jwtTokenProvider;
  private TokenBlacklistRepository tokenBlacklistRepository;
  private AuthenticationManager authenticationManager;

  @Autowired
  public AuthServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager,
                         JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder,
                         ForgotPasswordRepository forgotPasswordRepository,
                         TokenBlacklistRepository tokenBlacklistRepository) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.forgotPasswordRepository = forgotPasswordRepository;
    this.tokenBlacklistRepository = tokenBlacklistRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User signup(User user) {
    String email = user.getEmail();
    if(!userRepository.existsByEmail(email)) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));

      CompletableFuture.supplyAsync(() -> {
        // TODO: Invoke the mail service asynchronously
        return null;
      });
      return userRepository.save(user);
    }
    else {
      throw new CustomException("Email already exists", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  public LoginResponseDTO login(LoginRequestDTO user) {

    String username = user.getEmail();
    String password = user.getPassword();

    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
      User loggedInUser = userRepository.findByUserName(username).orElseThrow(() -> new CustomException("Invalid username/password supplied...", HttpStatus.UNPROCESSABLE_ENTITY));
      String token = jwtTokenProvider.createToken(username);
      LoginResponseDTO responseDTO = new LoginResponseDTO(
              loggedInUser.getFirstName(), loggedInUser.getLastName(), loggedInUser.getEmail(),
              null, loggedInUser.getPhone(), loggedInUser.getRoles());
      responseDTO.setToken(token);
      return responseDTO;
    }
    catch (AuthenticationException e) {
      throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  @Override
  public void initiateForgotPassword(ForgotPasswordRequestDTO fg) {
    boolean exists = userRepository.existsByEmail(fg.getEmail());
    if (exists) {
      ForgotPassword fp = new ForgotPassword();
      fp.setEmail(fg.getEmail());
      String code = RandomStringUtils.randomNumeric(5);
      fp.setCode(code);
      fp.setValid(true);
      fp.setValidity(LocalDateTime.now().plusMinutes(15));
      forgotPasswordRepository.save(fp);
      //TODO: Invoke the mail service to send new password to user
    }
    else {
      throw new CustomException("Email does not exist", HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public ForgotPassword verifyForgotPasswordCode(VerifyForgotPasswordDTO vfp) {
    ForgotPassword fp = forgotPasswordRepository.findByEmailAndCode(vfp.getEmail(), vfp.getCode())
        .orElseThrow(() -> new CustomException("Invalid code and email supplied", HttpStatus.BAD_REQUEST));
    if (!fp.getValidity().isBefore(LocalDateTime.now()) && !fp.isValid()) {
      throw new CustomException("Code has expired", HttpStatus.BAD_REQUEST);
    }
    return fp;
  }

  @Override
  public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
    // TODO: verifiy that user is not suspended for all reset password operations
    VerifyForgotPasswordDTO vfp = new VerifyForgotPasswordDTO();
    vfp.setCode(resetPasswordDTO.getCode());
    vfp.setEmail(resetPasswordDTO.getEmail());
    ForgotPassword fp = verifyForgotPasswordCode(vfp);
    fp.setValid(false);
    forgotPasswordRepository.save(fp);
    User user = userRepository.findByUserName(resetPasswordDTO.getEmail())
        .orElseThrow(() -> new CustomException("User does not exist", HttpStatus.BAD_REQUEST));
    user.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));
    userRepository.save(user);
  }

  @Override
  public void changePassword(ChangePasswordDTO changePasswordDTO, String userEmail) {
    User user = userRepository.findByUserName(userEmail).orElseThrow(() -> new CustomException("You are not authorized", HttpStatus.UNAUTHORIZED));
    user.setPassword(passwordEncoder.encode(changePasswordDTO.getPassword()));
    userRepository.save(user);
  }

  @Override
  public void logout(HttpServletRequest request) {
    String token = jwtTokenProvider.resolveToken(request);
    String email = jwtTokenProvider.getEmail(token);
    TokenBlacklist tokenBlacklist = new TokenBlacklist();
    tokenBlacklist.setToken(token);
    tokenBlacklist.setEmail(email);
    tokenBlacklistRepository.save(tokenBlacklist);
  }
}
