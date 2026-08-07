package yosadchuk.needle.flow.mapper;

import org.springframework.stereotype.Component;
import yosadchuk.needle.flow.model.dto.CreateManufacturerDto;
import yosadchuk.needle.flow.model.dto.ManufacturerResponseDto;
import yosadchuk.needle.flow.model.entity.Manufacturer;

@Component
public class ManufacturerMapper {

    public ManufacturerResponseDto toDto(Manufacturer entity){
        if(entity ==null) return null;
        return ManufacturerResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public Manufacturer toEntity(CreateManufacturerDto dto){
        if(dto ==null) return null;
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(dto.name());
        return manufacturer;
    }

    public void updateEntityFromDto(CreateManufacturerDto dto, Manufacturer entity){
        entity.setName(dto.name());
    }
}
