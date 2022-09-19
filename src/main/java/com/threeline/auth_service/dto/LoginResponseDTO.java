package com.threeline.auth_service.dto;

import com.threeline.auth_service.entity.Role;
import lombok.Data;

import java.util.List;

/**
 * LoginResponseDTO
 */

@Data
public class LoginResponseDTO extends UserRequestDTO {
    private String token;

    public LoginResponseDTO(String firstName, String lastName, String email, String password, String phone, List<Role> roles) {
        super(firstName, lastName, email, null, phone, roles);
    }

}
