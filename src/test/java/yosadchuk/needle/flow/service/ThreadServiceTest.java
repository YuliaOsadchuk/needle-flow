package yosadchuk.needle.flow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yosadchuk.needle.flow.exception.ResourceAlreadyExistsException;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.mapper.ThreadMapper;
import yosadchuk.needle.flow.model.dto.CreateThreadDto;
import yosadchuk.needle.flow.model.dto.ThreadResponseDto;
import yosadchuk.needle.flow.model.entity.Manufacturer;
import yosadchuk.needle.flow.model.entity.Thread;
import yosadchuk.needle.flow.repository.InventoryRepository;
import yosadchuk.needle.flow.repository.ManufacturerRepository;
import yosadchuk.needle.flow.repository.ThreadRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThreadServiceTest {

    @Mock
    private ThreadRepository threadRepository;
    @Mock
    private ManufacturerRepository manufacturerRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private ThreadMapper mapper;

    @InjectMocks
    private ThreadService threadService;

    @Test
    void findById_shouldReturnDto_whenThreadExists() {
        Thread thread = new Thread();
        thread.setId(1);
        ThreadResponseDto dto = new ThreadResponseDto(1, "310", "Black", null, null);

        when(threadRepository.findById(1)).thenReturn(Optional.of(thread));
        when(mapper.toDto(thread)).thenReturn(dto);

        ThreadResponseDto result = threadService.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.code()).isEqualTo("310");
    }

    @Test
    void findById_shouldThrow_whenThreadNotFound() {
        when(threadRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> threadService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_shouldThrow_whenCodeAlreadyExistsForManufacturer() {
        CreateThreadDto dto = new CreateThreadDto("310", "Black", 1);
        when(threadRepository.existsByCodeAndManufacturerId("310", 1)).thenReturn(true);

        assertThatThrownBy(() -> threadService.create(dto))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(threadRepository, never()).save(any());
    }

    @Test
    void create_shouldThrow_whenManufacturerNotFound() {
        CreateThreadDto dto = new CreateThreadDto("310", "Black", 99);
        when(threadRepository.existsByCodeAndManufacturerId("310", 99)).thenReturn(false);
        when(manufacturerRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> threadService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(threadRepository, never()).save(any());
    }

    @Test
    void create_shouldSaveThreadAndCreateEmptyInventory_whenValid() {
        CreateThreadDto dto = new CreateThreadDto("310", "Black", 1);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1);
        Thread entity = new Thread();

        when(threadRepository.existsByCodeAndManufacturerId("310", 1)).thenReturn(false);
        when(manufacturerRepository.findById(1)).thenReturn(Optional.of(manufacturer));
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(new ThreadResponseDto(1, "310", "Black", null, null));

        threadService.create(dto);

        verify(threadRepository).save(entity);
        verify(inventoryRepository).save(any());
    }

    @Test
    void update_shouldNotCheckDuplicate_whenCodeAndManufacturerUnchanged() {
        Thread entity = new Thread();
        entity.setId(1);
        entity.setCode("310");
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1);
        entity.setManufacturer(manufacturer);

        CreateThreadDto dto = new CreateThreadDto("310", "Black", 1);
        when(threadRepository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(new ThreadResponseDto(1, "310", "Black", null, null));

        threadService.update(1, dto);

        verify(threadRepository, never()).existsByCodeAndManufacturerId(any(), any());
    }

    @Test
    void update_shouldThrow_whenNewCodeAlreadyExistsForManufacturer() {
        Thread entity = new Thread();
        entity.setId(1);
        entity.setCode("310");
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1);
        entity.setManufacturer(manufacturer);

        CreateThreadDto dto = new CreateThreadDto("999", "New color", 1);
        when(threadRepository.findById(1)).thenReturn(Optional.of(entity));
        when(threadRepository.existsByCodeAndManufacturerId("999", 1)).thenReturn(true);

        assertThatThrownBy(() -> threadService.update(1, dto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    void delete_shouldThrow_whenThreadNotFound() {
        when(threadRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> threadService.delete(99))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(threadRepository, never()).deleteById(any());
    }

    @Test
    void delete_shouldCallRepository_whenThreadExists() {
        when(threadRepository.existsById(1)).thenReturn(true);

        threadService.delete(1);

        verify(threadRepository).deleteById(1);
    }
}