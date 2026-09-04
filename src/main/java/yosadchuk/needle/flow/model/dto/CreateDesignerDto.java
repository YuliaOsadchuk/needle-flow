package yosadchuk.needle.flow.model.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDesignerDto(
        @NotBlank(message = "Name is required")
        String name) {
}
