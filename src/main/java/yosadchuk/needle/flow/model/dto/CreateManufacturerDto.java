package yosadchuk.needle.flow.model.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateManufacturerDto(
        @NotBlank(message = "Name is required")
        String name) {
}
