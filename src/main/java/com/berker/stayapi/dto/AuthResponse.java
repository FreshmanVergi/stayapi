package com.berker.stayapi.dto;

public class AuthResponse {
    private String token;
    private String username;
    private String role;

    public AuthResponse() {}
    public AuthResponse(String token, String username, String role) {
        this.token = token; this.username = username; this.role = role;
    }
    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRole() { return role; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String token, username, role;
        public Builder token(String t) { this.token = t; return this; }
        public Builder username(String u) { this.username = u; return this; }
        public Builder role(String r) { this.role = r; return this; }
        public AuthResponse build() { return new AuthResponse(token, username, role); }
    }
}
