package com.aurionpro.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AddCommentRequest {

    @NotBlank(message = "Comment content is required")
    private String content;
}