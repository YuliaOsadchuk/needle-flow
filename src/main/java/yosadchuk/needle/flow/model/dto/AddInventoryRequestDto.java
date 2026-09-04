package yosadchuk.needle.flow.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddInventoryRequestDto(
        @NotNull(message = "Thread is required")
        Integer threadId,
        @NotNull(message = "Skeins is required")
        @Min(value = 0, message = "Skeins cannot be negative")
        Integer addSkeins,
        @NotNull(message = "Meters is required")
        @DecimalMin(value = "0.0", message = "Meters cannot be negative")
        BigDecimal addBobbinMeters) {
}
