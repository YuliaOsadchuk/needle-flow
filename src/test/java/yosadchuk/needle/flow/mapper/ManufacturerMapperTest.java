package yosadchuk.needle.flow.mapper;

import org.junit.jupiter.api.Test;
import yosadchuk.needle.flow.model.dto.CreateManufacturerDto;
import yosadchuk.needle.flow.model.entity.Manufacturer;

import static org.assertj.core.api.Assertions.assertThat;

public class ManufacturerMapperTest {

    private final ManufacturerMapper mapper = new ManufacturerMapper();

    @Test
    void toDto_shouldMapAllFields() {
        Manufacturer entity = new Manufacturer(1, "yermakova");
        var dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("yermakova");
    }

    @Test
    void toDto_shouldReturnNull_whenEntityIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_shouldMapName() {
        CreateManufacturerDto dto = new CreateManufacturerDto("yermakova");
        Manufacturer entity = mapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("yermakova");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void updateEntityFromDto_shouldOverwriteName() {
        Manufacturer entity = new Manufacturer(1, "old name");
        mapper.updateEntityFromDto(new CreateManufacturerDto("new name"), entity);

        assertThat(entity.getName()).isEqualTo("new name");
        assertThat(entity.getId()).isEqualTo(1);
    }

}
