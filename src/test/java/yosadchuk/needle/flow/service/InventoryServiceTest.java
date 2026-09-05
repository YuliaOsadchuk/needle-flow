package yosadchuk.needle.flow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.model.dto.AddInventoryRequestDto;
import yosadchuk.needle.flow.model.entity.Inventory;
import yosadchuk.needle.flow.repository.InventoryRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void addStock_shouldThrow_whenInventoryNotFound() {
        AddInventoryRequestDto dto = new AddInventoryRequestDto(99, 1, BigDecimal.ONE);
        when(inventoryRepository.findByThreadId(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.addStock(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void addStock_shouldIncreaseExistingQuantities() {
        Inventory inventory = new Inventory(1, null, 2, BigDecimal.valueOf(5));
        AddInventoryRequestDto dto = new AddInventoryRequestDto(1, 3, BigDecimal.valueOf(2.5));

        when(inventoryRepository.findByThreadId(1)).thenReturn(Optional.of(inventory));

        inventoryService.addStock(dto);

        assertThat(inventory.getSkeinQuantity()).isEqualTo(5);
        assertThat(inventory.getBobbinQuantity()).isEqualByComparingTo(BigDecimal.valueOf(7.5));
    }

    @Test
    void updateStock_shouldThrow_whenInventoryNotFound() {
        AddInventoryRequestDto dto = new AddInventoryRequestDto(99, 1, BigDecimal.ONE);
        when(inventoryRepository.findByThreadId(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.updateStock(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateStock_shouldOverwriteQuantities_notAdd() {
        Inventory inventory = new Inventory(1, null, 10, BigDecimal.valueOf(20));
        AddInventoryRequestDto dto = new AddInventoryRequestDto(1, 3, BigDecimal.valueOf(2.5));

        when(inventoryRepository.findByThreadId(1)).thenReturn(Optional.of(inventory));

        inventoryService.updateStock(dto);

        assertThat(inventory.getSkeinQuantity()).isEqualTo(3);
        assertThat(inventory.getBobbinQuantity()).isEqualByComparingTo(BigDecimal.valueOf(2.5));
    }
}