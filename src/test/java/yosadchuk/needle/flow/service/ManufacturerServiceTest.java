package yosadchuk.needle.flow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yosadchuk.needle.flow.exception.ResourceAlreadyExistsException;
import yosadchuk.needle.flow.exception.ResourceInUseException;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.mapper.ManufacturerMapper;
import yosadchuk.needle.flow.model.dto.CreateManufacturerDto;
import yosadchuk.needle.flow.model.dto.ManufacturerResponseDto;
import yosadchuk.needle.flow.model.entity.Manufacturer;
import yosadchuk.needle.flow.repository.ManufacturerRepository;
import yosadchuk.needle.flow.repository.ThreadRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManufacturerServiceTest {

    @Mock
    private ManufacturerRepository manufacturerRepository;
    @Mock
    private ThreadRepository threadRepository;
    @Mock
    private ManufacturerMapper manufacturerMapper;

    @InjectMocks
    private ManufacturerService manufacturerService;

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(manufacturerRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> manufacturerService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_shouldThrow_whenNameAlreadyExists() {
        CreateManufacturerDto dto = new CreateManufacturerDto("DMC");
        when(manufacturerRepository.existsByName("DMC")).thenReturn(true);

        assertThatThrownBy(() -> manufacturerService.create(dto))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(manufacturerRepository, never()).save(any());
    }

    @Test
    void create_shouldSave_whenNameIsUnique() {
        CreateManufacturerDto dto = new CreateManufacturerDto("DMC");
        Manufacturer entity = new Manufacturer(null, "DMC");
        Manufacturer saved = new Manufacturer(1, "DMC");

        when(manufacturerRepository.existsByName("DMC")).thenReturn(false);
        when(manufacturerMapper.toEntity(dto)).thenReturn(entity);
        when(manufacturerRepository.save(entity)).thenReturn(saved);
        when(manufacturerMapper.toDto(saved)).thenReturn(new ManufacturerResponseDto(1, "DMC"));

        ManufacturerResponseDto result = manufacturerService.create(dto);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void update_shouldThrow_whenNewNameAlreadyExists() {
        Manufacturer entity = new Manufacturer(1, "DMC");
        CreateManufacturerDto dto = new CreateManufacturerDto("Anchor");

        when(manufacturerRepository.findById(1)).thenReturn(Optional.of(entity));
        when(manufacturerRepository.existsByName("Anchor")).thenReturn(true);

        assertThatThrownBy(() -> manufacturerService.update(1, dto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(manufacturerRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> manufacturerService.delete(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldThrow_whenManufacturerHasAssociatedThreads() {
        when(manufacturerRepository.existsById(1)).thenReturn(true);
        when(threadRepository.existsByManufacturerId(1)).thenReturn(true);

        assertThatThrownBy(() -> manufacturerService.delete(1))
                .isInstanceOf(ResourceInUseException.class);

        verify(manufacturerRepository, never()).deleteById(any());
    }

    @Test
    void delete_shouldCallRepository_whenNoAssociatedThreads() {
        when(manufacturerRepository.existsById(1)).thenReturn(true);
        when(threadRepository.existsByManufacturerId(1)).thenReturn(false);

        manufacturerService.delete(1);

        verify(manufacturerRepository).deleteById(1);
    }
}