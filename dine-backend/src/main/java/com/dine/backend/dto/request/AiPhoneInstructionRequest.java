package com.dine.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiPhoneInstructionRequest {

    @NotBlank(message = "Instruction content is required")
    private String instruction;

    private Integer sortOrder;
}
