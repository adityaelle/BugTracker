package com.aurionpro.dto.response;

import com.aurionpro.entity.enums.Department;
import com.aurionpro.entity.enums.UserRole;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private UserRole role;
    private Department department;
    private boolean isActive;
}