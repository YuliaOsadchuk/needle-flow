package yosadchuk.needle.flow.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DesignThreadRequestDto(
        @NotNull(message = "Thread is required")
        Integer threadId,
        @NotNull(message = "Meters is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Meters must be greater than 0")
        BigDecimal requiredMeters) {
}
