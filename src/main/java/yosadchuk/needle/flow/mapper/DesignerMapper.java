package yosadchuk.needle.flow.mapper;

import org.springframework.stereotype.Component;
import yosadchuk.needle.flow.model.dto.CreateDesignerDto;
import yosadchuk.needle.flow.model.dto.DesignerResponseDto;
import yosadchuk.needle.flow.model.entity.Designer;

@Component
public class DesignerMapper {

    public DesignerResponseDto toDto(Designer entity) {
        if (entity == null) return null;
        return DesignerResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public Designer toEntity(CreateDesignerDto dto) {
        if (dto == null) return null;
        Designer designer = new Designer();
        designer.setName(dto.name());
        return designer;
    }

    public void updateEntityFromDto(CreateDesignerDto dto, Designer entity) {
        entity.setName(dto.name());
    }
}
