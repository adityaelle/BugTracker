package com.aurionpro.dto.request;

import com.aurionpro.entity.enums.Environment;
import com.aurionpro.entity.enums.TicketCategory;
import com.aurionpro.entity.enums.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateTicketRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private TicketCategory category;

    @NotNull(message = "Priority is required")
    private TicketPriority priority;

    @NotNull(message = "Environment is required")
    private Environment environment;

    private String applicationModule;
    private String version;
    private String browser;
    private String stepsToReproduce;
    private String expectedResult;
    private String actualResult;
}