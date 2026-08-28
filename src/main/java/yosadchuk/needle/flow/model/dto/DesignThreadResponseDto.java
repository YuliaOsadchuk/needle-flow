package yosadchuk.needle.flow.model.dto;

import java.math.BigDecimal;

public record DesignThreadResponseDto(Integer id, Integer threadId, String threadCode, String threadName,
                                      BigDecimal requiredMeters, BigDecimal availableMeters, boolean isSufficient) {
}
