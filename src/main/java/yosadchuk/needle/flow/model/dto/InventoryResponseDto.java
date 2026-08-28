package yosadchuk.needle.flow.model.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record InventoryResponseDto(Integer id, Integer skeinsQuantity, BigDecimal bobbinMeters, BigDecimal totalMeters) {
}