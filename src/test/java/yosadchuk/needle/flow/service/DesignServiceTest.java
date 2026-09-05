package yosadchuk.needle.flow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yosadchuk.needle.flow.exception.ResourceAlreadyExistsException;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.mapper.DesignMapper;
import yosadchuk.needle.flow.model.dto.*;
import yosadchuk.needle.flow.model.entity.*;
import yosadchuk.needle.flow.model.entity.Thread;
import yosadchuk.needle.flow.repository.DesignRepository;
import yosadchuk.needle.flow.repository.DesignThreadRepository;
import yosadchuk.needle.flow.repository.DesignerRepository;
import yosadchuk.needle.flow.repository.ThreadRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesignServiceTest {

    @Mock
    private DesignRepository designRepository;
    @Mock
    private DesignerRepository designerRepository;
    @Mock
    private ThreadRepository threadRepository;
    @Mock
    private DesignThreadRepository designThreadRepository;
    @Mock
    private DesignMapper designMapper;

    @InjectMocks
    private DesignService designService;

    @Test
    void findById_shouldThrow_whenDesignNotFound() {
        when(designRepository.findByIdWithDetails(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> designService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_shouldThrow_whenDesignNameAlreadyExistsForDesigner() {
        CreateDesignDto dto = new CreateDesignDto("Jasmine", 1, DesignStatus.IN_PROGRESS, List.of());
        when(designRepository.existsByNameAndDesignerId("Jasmine", 1)).thenReturn(true);

        assertThatThrownBy(() -> designService.create(dto))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(designRepository, never()).save(any());
    }

    @Test
    void create_shouldThrow_whenDesignerNotFound() {
        CreateDesignDto dto = new CreateDesignDto("Jasmine", 99, DesignStatus.IN_PROGRESS, List.of());
        when(designRepository.existsByNameAndDesignerId("Jasmine", 99)).thenReturn(false);
        when(designerRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> designService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_shouldThrow_whenThreadInListDoesNotExist() {
        Designer designer = new Designer(1, "Test Designer");
        DesignThreadRequestDto threadDto = new DesignThreadRequestDto(99, BigDecimal.TEN);
        CreateDesignDto dto = new CreateDesignDto("Jasmine", 1, DesignStatus.IN_PROGRESS, List.of(threadDto));
        Design entity = new Design(null, "Jasmine", null, DesignStatus.IN_PROGRESS, null, null);

        when(designRepository.existsByNameAndDesignerId("Jasmine", 1)).thenReturn(false);
        when(designerRepository.findById(1)).thenReturn(Optional.of(designer));
        when(designMapper.toEntity(dto)).thenReturn(entity);
        when(threadRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> designService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(designRepository, never()).save(any());
    }

    @Test
    void create_shouldAttachThreadsToDesign_whenValid() {
        Designer designer = new Designer(1, "Test Designer");
        Thread thread = new Thread(5, "310", "Black", null, null);
        DesignThreadRequestDto threadDto = new DesignThreadRequestDto(5, BigDecimal.TEN);
        CreateDesignDto dto = new CreateDesignDto("Jasmine", 1, DesignStatus.IN_PROGRESS, List.of(threadDto));
        Design entity = new Design(null, "Jasmine", null, DesignStatus.IN_PROGRESS, null, null);

        when(designRepository.existsByNameAndDesignerId("Jasmine", 1)).thenReturn(false);
        when(designerRepository.findById(1)).thenReturn(Optional.of(designer));
        when(designMapper.toEntity(dto)).thenReturn(entity);
        when(threadRepository.findById(5)).thenReturn(Optional.of(thread));
        when(designRepository.save(entity)).thenReturn(entity);
        when(designMapper.toDto(entity)).thenReturn(
                new DesignResponseDto(1, "Jasmine", null, null, List.of(), false, ""));

        designService.create(dto);

        assertThat(entity.getThreads()).hasSize(1);
        assertThat(entity.getThreads().get(0).getThread()).isEqualTo(thread);
        assertThat(entity.getThreads().get(0).getRequiredMeters()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void update_shouldNotCheckDuplicate_whenNameAndDesignerUnchanged() {
        Designer designer = new Designer(1, "Test Designer");
        Design entity = new Design(1, "Jasmine", designer, DesignStatus.IN_PROGRESS, new java.util.ArrayList<>(), null);
        CreateDesignDto dto = new CreateDesignDto("Jasmine", 1, DesignStatus.IN_PROGRESS, List.of());

        when(designRepository.findById(1)).thenReturn(Optional.of(entity));
        when(threadRepository.findAllById(List.of())).thenReturn(List.of());
        when(designMapper.toDto(entity)).thenReturn(new DesignResponseDto(1, "Jasmine", null, null,
                List.of(), false, ""));

        designService.update(1, dto);

        verify(designRepository, never()).existsByNameAndDesignerId(any(), any());
    }

    @Test
    void update_shouldThrow_whenNewNameAlreadyExistsForDesigner() {
        Designer designer = new Designer(1, "Test Designer");
        Design entity = new Design(1, "Jasmine", designer, DesignStatus.IN_PROGRESS, new java.util.ArrayList<>(), null);
        CreateDesignDto dto = new CreateDesignDto("New Name", 1, DesignStatus.IN_PROGRESS, List.of());

        when(designRepository.findById(1)).thenReturn(Optional.of(entity));
        when(designRepository.existsByNameAndDesignerId("New Name", 1)).thenReturn(true);

        assertThatThrownBy(() -> designService.update(1, dto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    void delete_shouldThrow_whenDesignNotFound() {
        when(designRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> designService.delete(99))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(designRepository, never()).deleteById(any());
    }

    @Test
    void delete_shouldCallRepository_whenDesignHasNoImage() {
        Design entity = new Design(1, "Jasmine", null, DesignStatus.IN_PROGRESS, null, null);
        when(designRepository.findById(1)).thenReturn(Optional.of(entity));

        designService.delete(1);

        verify(designRepository).deleteById(1);
    }

    @Test
    void calculateShoppingList_shouldReturnEmptyList_whenDesignIdsIsEmpty() {
        List<ShoppingListItemDto> result = designService.calculateShoppingList(List.of());

        assertThat(result).isEmpty();
        verifyNoInteractions(designThreadRepository);
    }

    @Test
    void calculateShoppingList_shouldReturnEmptyList_whenDesignIdsIsNull() {
        List<ShoppingListItemDto> result = designService.calculateShoppingList(null);

        assertThat(result).isEmpty();
    }

    @Test
    void calculateShoppingList_shouldAggregateRequiredMetersAcrossDesigns_andCalculateToBuy() {
        Manufacturer manufacturer = new Manufacturer(1, "DMC");
        Thread thread = new Thread(5, "310", "Black", manufacturer, null);
        Inventory inventory = new Inventory(1, thread, 2, BigDecimal.valueOf(3));
        thread.setInventory(inventory);

        DesignThread dt1 = new DesignThread(1, null, thread, BigDecimal.valueOf(10));
        DesignThread dt2 = new DesignThread(2, null, thread, BigDecimal.valueOf(15));

        when(designThreadRepository.findByDesignIdIn(List.of(1, 2))).thenReturn(List.of(dt1, dt2));

        List<ShoppingListItemDto> result = designService.calculateShoppingList(List.of(1, 2));

        assertThat(result).hasSize(1);
        ShoppingListItemDto item = result.get(0);
        assertThat(item.requiredQuantity()).isEqualByComparingTo(BigDecimal.valueOf(25)); // 10 + 15
        assertThat(item.inStockQuantity()).isEqualByComparingTo(BigDecimal.valueOf(19));  // 2*8 + 3
        assertThat(item.toBuyQuantity()).isEqualByComparingTo(BigDecimal.valueOf(6));     // 25 - 19
    }
}
