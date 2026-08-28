package yosadchuk.needle.flow.mapper;

import org.springframework.stereotype.Component;
import yosadchuk.needle.flow.model.dto.InventoryResponseDto;
import yosadchuk.needle.flow.model.entity.Inventory;

import java.math.BigDecimal;

@Component
public class InventoryMapper {

    public InventoryResponseDto toDto(Inventory entity) {
        return InventoryResponseDto.builder()
                .id(entity.getId())
                .skeinsQuantity(entity.getSkeinQuantity())
                .bobbinMeters(entity.getBobbinQuantity())
                .totalMeters(entity.getBobbinQuantity().add(BigDecimal.valueOf(entity.getSkeinQuantity() * 8.0)))
                .build();
    }
}