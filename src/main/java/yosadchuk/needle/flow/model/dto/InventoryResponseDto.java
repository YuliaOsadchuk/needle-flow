package yosadchuk.needle.flow.model.dto;

import lombok.Builder;

@Builder
public record InventoryResponseDto(Integer id, Integer skeinsQuantity, Double bobbinMeters, Double totalMeters) {
}