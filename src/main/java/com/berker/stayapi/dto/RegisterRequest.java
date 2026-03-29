package com.berker.stayapi.dto;

import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {
    @NotBlank private String username;
    @NotBlank private String password;
    @NotBlank private String role;

    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
    public String getRole() { return role; }
    public void setRole(String r) { this.role = r; }
}
