package yosadchuk.needle.flow.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateThreadDto(
        @NotBlank(message = "Code is required")
        String code,
        @NotBlank(message = "Name is required")
        String name,
        @NotNull(message = "Manufacturer is required")
        Integer manufacturerId) { }
