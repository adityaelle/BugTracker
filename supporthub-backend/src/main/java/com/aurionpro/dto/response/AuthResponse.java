package com.aurionpro.dto.response;

import com.aurionpro.entity.enums.UserRole;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private UserRole role;
}