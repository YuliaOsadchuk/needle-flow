package yosadchuk.needle.flow.model.dto;

import java.math.BigDecimal;

public record DesignThreadRequestDto(Integer threadId, BigDecimal requiredMeters) {
}
