package com.threeline.auth_service.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.threeline.auth_service.entity.AuditModel;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "permissions")
public class Permission extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Permission should not be blank")
    @Column(nullable = false, unique = true)
    private String permission;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Role> roles;

    public void addRole(Role role) {
	if (roles == null) {
	    roles = new ArrayList<>();
	}
	roles.add(role);
    }
    public void removeRole(Role role) {
	if (roles == null) {
	    roles = new ArrayList<>();
	}
	roles.remove(role);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        return id != null && id.equals(((Permission) o).id);
    }
 
    @Override
    public int hashCode() {
        return 31;
    }
   
}