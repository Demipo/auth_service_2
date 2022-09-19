package com.threeline.auth_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles", uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Role extends AuditModel implements GrantedAuthority {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name should not be blank")
    @Column(unique = true)
    private String name;
    private String description;
    private String status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private List<Permission> permissions;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @Override
    public String getAuthority() {
	return name;
    }

    public void addUser(User user) {
	/*
	 * if (users == null) { users = new ArrayList<>(); }
	 */
	users.add(user);
    }

    public void removeUser(User user) {
	/*
	 * if (users == null) { users = new ArrayList<>(); }
	 */
	users.remove(user);
    }

    public void addPermission(Permission permission) {

	if (permissions == null) {
	    permissions = new ArrayList<>();
	}
	permissions.add(permission);
	permission.addRole(this);
    }

    public void removePermission(Permission permission) {
	if (permissions == null) {
	    permissions = new ArrayList<>();
	}
	permissions.remove(permission);
	permission.removeRole(this);
    }

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (!(o instanceof Role))
	    return false;
	return id != null && id.equals(((Role) o).getId());
    }

    @Override
    public int hashCode() {
	return 31;
    }
}
