package com.threeline.auth_service.dto;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.threeline.auth_service.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * UserRequestDTO
 */

@Data
@NoArgsConstructor
public class UserRequestDTO {
  @NotBlank
  @JsonAlias({ "first_name" })
  private String firstName;
  @NotBlank
  @JsonAlias({ "last_name" })
  private String lastName;
  @Email
  @NotBlank
  private String email;
  @NotBlank
  @Size(min = 6)
  private String password;

  @NotBlank
  @Size(min = 10)
  private String phone;

  private List<Role> roles;


  public UserRequestDTO(@NotBlank String firstName, @NotBlank String lastName,
      @Email @NotBlank String email, @NotBlank @Size(min = 6) String password,
      @NotBlank @Size(min = 10) String phone, List<Role> roles) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.phone = phone;
    this.roles = roles;
  }


}