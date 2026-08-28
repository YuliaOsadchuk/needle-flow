package yosadchuk.needle.flow.model.dto;

import java.math.BigDecimal;

public record AddInventoryRequestDto(Integer threadId, Integer addSkeins, BigDecimal addBobbinMeters) {
}
