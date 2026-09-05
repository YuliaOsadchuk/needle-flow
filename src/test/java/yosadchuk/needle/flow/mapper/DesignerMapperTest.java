package yosadchuk.needle.flow.mapper;

import org.junit.jupiter.api.Test;
import yosadchuk.needle.flow.model.dto.CreateDesignerDto;
import yosadchuk.needle.flow.model.entity.Designer;

import static org.assertj.core.api.Assertions.assertThat;

class DesignerMapperTest {

    private final DesignerMapper mapper = new DesignerMapper();

    @Test
    void toDto_shouldMapAllFields() {
        Designer entity = new Designer(1, "yermakova");
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
        CreateDesignerDto dto = new CreateDesignerDto("yermakova");
        Designer entity = mapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("yermakova");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void updateEntityFromDto_shouldOverwriteName() {
        Designer entity = new Designer(1, "old name");
        mapper.updateEntityFromDto(new CreateDesignerDto("new name"), entity);

        assertThat(entity.getName()).isEqualTo("new name");
        assertThat(entity.getId()).isEqualTo(1);
    }
}