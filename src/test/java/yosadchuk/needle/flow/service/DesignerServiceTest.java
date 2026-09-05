package yosadchuk.needle.flow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yosadchuk.needle.flow.exception.ResourceAlreadyExistsException;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.mapper.DesignerMapper;
import yosadchuk.needle.flow.model.dto.CreateDesignerDto;
import yosadchuk.needle.flow.model.dto.DesignerResponseDto;
import yosadchuk.needle.flow.model.entity.Designer;
import yosadchuk.needle.flow.repository.DesignerRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesignerServiceTest {

    @Mock
    private DesignerRepository designerRepository;
    @Mock
    private DesignerMapper designerMapper;

    @InjectMocks
    private DesignerService designerService;

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(designerRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> designerService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_shouldThrow_whenNameAlreadyExists() {
        CreateDesignerDto dto = new CreateDesignerDto("yermakova");
        when(designerRepository.existsByName("yermakova")).thenReturn(true);

        assertThatThrownBy(() -> designerService.create(dto))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(designerRepository, never()).save(any());
    }

    @Test
    void create_shouldSave_whenNameIsUnique() {
        CreateDesignerDto dto = new CreateDesignerDto("yermakova");
        Designer entity = new Designer(null, "yermakova");
        Designer saved = new Designer(1, "yermakova");

        when(designerRepository.existsByName("yermakova")).thenReturn(false);
        when(designerMapper.toEntity(dto)).thenReturn(entity);
        when(designerRepository.save(entity)).thenReturn(saved);
        when(designerMapper.toDto(saved)).thenReturn(new DesignerResponseDto(1, "yermakova"));

        DesignerResponseDto result = designerService.create(dto);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void update_shouldNotCheckDuplicate_whenNameOnlyDiffersInCase() {
        Designer entity = new Designer(1, "yermakova");
        CreateDesignerDto dto = new CreateDesignerDto("YERMAKOVA");

        when(designerRepository.findById(1)).thenReturn(Optional.of(entity));
        when(designerMapper.toDto(entity)).thenReturn(new DesignerResponseDto(1, "YERMAKOVA"));

        designerService.update(1, dto);

        verify(designerRepository, never()).existsByName(any());
    }

    @Test
    void update_shouldThrow_whenNewNameAlreadyExists() {
        Designer entity = new Designer(1, "yermakova");
        CreateDesignerDto dto = new CreateDesignerDto("petrova");

        when(designerRepository.findById(1)).thenReturn(Optional.of(entity));
        when(designerRepository.existsByName("petrova")).thenReturn(true);

        assertThatThrownBy(() -> designerService.update(1, dto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(designerRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> designerService.delete(99))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(designerRepository, never()).deleteById(any());
    }

    @Test
    void delete_shouldCallRepository_whenExists() {
        when(designerRepository.existsById(1)).thenReturn(true);

        designerService.delete(1);

        verify(designerRepository).deleteById(1);
    }
}
