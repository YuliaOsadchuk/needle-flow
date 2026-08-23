package yosadchuk.needle.flow.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yosadchuk.needle.flow.model.dto.CreateDesignDto;
import yosadchuk.needle.flow.model.dto.DesignResponseDto;
import yosadchuk.needle.flow.model.entity.Design;

@Component
@RequiredArgsConstructor
public class DesignMapper {

    private final DesignerMapper designerMapper;

    public DesignResponseDto toDto(Design entity) {
        if (entity == null) return null;
        return DesignResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .designer(designerMapper.toDto(entity.getDesigner()))
                .status(entity.getStatus())
                .build();
    }

    public Design toEntity(CreateDesignDto dto) {
        if (dto == null) return null;
        Design entity = new Design();
        entity.setName(dto.name());
        entity.setStatus(dto.status());
        return entity;
    }

    public void updateEntityFromDto(CreateDesignDto dto, Design entity) {
        entity.setName(dto.name());
        entity.setStatus(dto.status());
    }
}
