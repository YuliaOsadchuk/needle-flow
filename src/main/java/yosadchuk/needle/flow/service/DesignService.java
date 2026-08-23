package yosadchuk.needle.flow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yosadchuk.needle.flow.exception.ResourceAlreadyExistsException;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.mapper.DesignMapper;
import yosadchuk.needle.flow.model.dto.CreateDesignDto;
import yosadchuk.needle.flow.model.dto.DesignResponseDto;
import yosadchuk.needle.flow.model.entity.Design;
import yosadchuk.needle.flow.model.entity.Designer;
import yosadchuk.needle.flow.repository.DesignRepository;
import yosadchuk.needle.flow.repository.DesignerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DesignService {

    private final DesignRepository designRepository;
    private final DesignerRepository designerRepository;
    private final DesignMapper designMapper;

    public List<DesignResponseDto> findAll() {
        return designRepository.findAll().stream().map(designMapper::toDto).toList();
    }

    public DesignResponseDto findById(Integer id) {
        return designRepository.findById(id).map(designMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Design with id " + id + " not found"));
    }

    @Transactional
    public DesignResponseDto save(CreateDesignDto dto) {
        if (designRepository.existsByNameAndDesignerId(dto.name(), dto.designer())) {
            throw new ResourceAlreadyExistsException("Design with name " + dto.name() + " for designer already exists");
        }

        Designer designer = designerRepository.findById(dto.designer())
                .orElseThrow(() -> new ResourceNotFoundException("Designer with id " + dto.designer() + " not found"));
        Design entity = designMapper.toEntity(dto);
        entity.setDesigner(designer);
        return designMapper.toDto(designRepository.save(entity));
    }

    @Transactional
    public DesignResponseDto update(Integer id, CreateDesignDto dto) {
        Design entity = designRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Design with id " + id + " not found"));

        boolean isNameOrDesignerChanged = !entity.getName().equals(dto.name())
                || !entity.getDesigner().getId().equals(dto.designer());

        if (isNameOrDesignerChanged && designRepository.existsByNameAndDesignerId(dto.name(), dto.designer())) {
            throw new ResourceAlreadyExistsException("Design with name " + dto.name() + " for designer already exists");
        }

        designMapper.updateEntityFromDto(dto, entity);
        if (!entity.getDesigner().getId().equals(dto.designer())) {
            Designer designer = designerRepository.findById(dto.designer())
                    .orElseThrow(() -> new ResourceNotFoundException("Designer with id " + dto.designer() + " not found"));
            entity.setDesigner(designer);
        }
        return designMapper.toDto(entity);
    }

    @Transactional
    public void delete(Integer id) {
        if (!designRepository.existsById(id)) {
            throw new ResourceNotFoundException("Design with id " + id + " not found");
        }

        designRepository.deleteById(id);
    }
}
