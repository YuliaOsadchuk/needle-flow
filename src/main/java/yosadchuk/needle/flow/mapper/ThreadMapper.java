package yosadchuk.needle.flow.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yosadchuk.needle.flow.model.dto.CreateThreadDto;
import yosadchuk.needle.flow.model.dto.ThreadResponseDto;
import yosadchuk.needle.flow.model.entity.Thread;

@Component
@RequiredArgsConstructor
public class ThreadMapper {

    private final ManufacturerMapper manufacturerMapper;
    private final InventoryMapper inventoryMapper;

    public ThreadResponseDto toDto(Thread entity) {
        if (entity == null) return null;
        return ThreadResponseDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .manufacturer(manufacturerMapper.toDto(entity.getManufacturer()))
                .inventory(inventoryMapper.toDto(entity.getInventory()))
                .build();
    }

    public Thread toEntity(CreateThreadDto dto) {
        if (dto == null) return null;
        Thread thread = new Thread();
        thread.setCode(dto.code());
        thread.setName(dto.name());
        return thread;
    }

    public void updateEntityFromDto(CreateThreadDto dto, Thread entity) {
        entity.setName(dto.name());
        entity.setCode(dto.code());
    }
}
