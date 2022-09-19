package com.threeline.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Setter
@Getter

@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String status;
    private Collection<PermissionDTO> permissions=new ArrayList<>();
    
    public void addPermission(PermissionDTO dto) {
	permissions.add(dto);
    }

    /**
     * @param id
     * @param name
     * @param status
     * @param description
     */
    public RoleResponseDTO(Long id, String name, String status, String description) {
	// TODO Auto-generated constructor stub
	this.id=id;
	this.name=name;
	this.status=status;
	this.description=description;
    }
    
   
}
