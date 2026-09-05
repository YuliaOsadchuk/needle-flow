package yosadchuk.needle.flow.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import yosadchuk.needle.flow.model.entity.Inventory;
import yosadchuk.needle.flow.model.entity.Manufacturer;
import yosadchuk.needle.flow.model.entity.Thread;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadMapperTest {

    private ThreadMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ThreadMapper(new ManufacturerMapper(), new InventoryMapper());
    }

    @Test
    void toDto_shouldMapNestedManufacturerAndInventory() {
        Manufacturer manufacturer = new Manufacturer(1, "DMC");
        Inventory inventory = new Inventory(1, null, 2, BigDecimal.valueOf(3));
        Thread entity = new Thread(5, "310", "Black", manufacturer, inventory);

        var dto = mapper.toDto(entity);

        assertThat(dto.manufacturer().name()).isEqualTo("DMC");
        assertThat(dto.inventory().totalMeters()).isEqualByComparingTo(BigDecimal.valueOf(19));
    }

    @Test
    void toDto_shouldNotThrow_whenInventoryIsNull() {
        Manufacturer manufacturer = new Manufacturer(1, "DMC");
        Thread entity = new Thread(5, "310", "Black", manufacturer, null);

        var dto = mapper.toDto(entity);

        assertThat(dto.inventory()).isNull();
        assertThat(dto.code()).isEqualTo("310");
    }

    @Test
    void toDto_shouldReturnNull_whenEntityIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }
}