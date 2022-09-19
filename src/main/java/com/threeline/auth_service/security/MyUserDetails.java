package com.threeline.auth_service.security;

import com.threeline.auth_service.entity.Role;
import com.threeline.auth_service.entity.User;
import com.threeline.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MyUserDetails
 */
@Service
public class MyUserDetails implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  private UserDetails GetDetails(String username, String password, List<Role> roles) {

    //obtain all permissions for roles, and with the params, create a userDetails instance
    List<String> allPermissions = roles.stream().map(r -> {
      List<String> pl = r.getPermissions().stream().map(p -> p.getPermission()).collect(Collectors.toList());
      pl.add("ROLE_" + r.getName());
      return pl;
    }).flatMap(Collection::stream).collect(Collectors.toList());

    return org.springframework.security.core.userdetails.User
            .withUsername(username)
            .password(password)
            .authorities(allPermissions.toArray(new String[0]))
            .accountExpired(false).accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build();
  }


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUserName(username).orElseThrow(() -> new  UsernameNotFoundException("User with '" + username + "' not found"));
    UserDetails ud = GetDetails(username, user.getPassword(), user.getRoles());
    return ud;
  }

}