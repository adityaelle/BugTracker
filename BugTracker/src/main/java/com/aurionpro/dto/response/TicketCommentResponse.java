package com.aurionpro.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketCommentResponse {
    private Long id;
    private String content;
    private UserResponse commentedBy;
    private LocalDateTime createdAt;
}