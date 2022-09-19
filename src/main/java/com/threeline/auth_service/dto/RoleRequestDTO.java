package com.threeline.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestDTO {
    
    @NotBlank(message = "Name should not be blank")
    private String name;
    private String description;
    private String status="ACTIVE";
    private Collection<String> permissions;
    

}
