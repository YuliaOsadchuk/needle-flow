package yosadchuk.needle.flow.mapper;

import org.junit.jupiter.api.Test;
import yosadchuk.needle.flow.model.entity.Inventory;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryMapperTest {

    private final InventoryMapper mapper = new InventoryMapper();

    @Test
    void toDto_shouldReturnNull_whenEntityIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toDto_shouldCalculateTotalMeters_asSkeinsTimes8PlusBobbin() {
        Inventory entity = new Inventory(1, null, 2, BigDecimal.valueOf(3.5));

        var dto = mapper.toDto(entity);

        assertThat(dto.skeinsQuantity()).isEqualTo(2);
        assertThat(dto.bobbinMeters()).isEqualByComparingTo(BigDecimal.valueOf(3.5));
        assertThat(dto.totalMeters()).isEqualByComparingTo(BigDecimal.valueOf(19.5));
    }

    @Test
    void toDto_shouldReturnZeroTotalMeters_whenSkeinsAndBobbinAreZero() {
        Inventory entity = new Inventory(1, null, 0, BigDecimal.ZERO);

        var dto = mapper.toDto(entity);

        assertThat(dto.totalMeters()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}