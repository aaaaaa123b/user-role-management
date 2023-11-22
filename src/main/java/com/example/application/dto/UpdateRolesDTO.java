package com.example.application.dto;

import com.example.application.enumeration.Role;

import java.util.Set;

public class UpdateRolesDTO {

    private Set<Role> roles;

    public UpdateRolesDTO() {
    }

    public UpdateRolesDTO(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UpdateRolesDTO{" +
                "roles=" + roles +
                '}';
    }
}
