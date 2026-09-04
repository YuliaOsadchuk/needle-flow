package yosadchuk.needle.flow.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import yosadchuk.needle.flow.model.entity.DesignStatus;

import java.util.List;

public record CreateDesignDto(
        @NotBlank(message = "Name is required")
        String name,
        @NotNull(message = "Designer should be chosen")
        Integer designer,
        DesignStatus status,
        @Valid
        List<DesignThreadRequestDto> threads) {
}
