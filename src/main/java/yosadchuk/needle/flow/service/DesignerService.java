package yosadchuk.needle.flow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yosadchuk.needle.flow.exception.ResourceAlreadyExistsException;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.mapper.DesignerMapper;
import yosadchuk.needle.flow.model.dto.CreateDesignerDto;
import yosadchuk.needle.flow.model.dto.DesignerResponseDto;
import yosadchuk.needle.flow.model.entity.Designer;
import yosadchuk.needle.flow.repository.DesignerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DesignerService {

    private final DesignerRepository designerRepository;
    private final DesignerMapper designerMapper;

    public List<DesignerResponseDto> findAll() {
        return designerRepository.findAll().stream().map(designerMapper::toDto).toList();
    }

    public DesignerResponseDto findById(Integer id) {
        return designerRepository.findById(id).map(designerMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Designer with id " + id + " not found"));
    }

    @Transactional
    public DesignerResponseDto create(CreateDesignerDto dto) {
        if (designerRepository.existsByName(dto.name())) {
            throw new ResourceAlreadyExistsException("Designer with name " + dto.name() + " already exists");
        }

        Designer savedEntity = designerRepository.save(designerMapper.toEntity(dto));
        return designerMapper.toDto(savedEntity);
    }

    @Transactional
    public DesignerResponseDto update(Integer id, CreateDesignerDto dto) {
        Designer entity = designerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designer with id " + id + " not found"));

        boolean isNameChanged = !entity.getName().equalsIgnoreCase(dto.name());
        if (isNameChanged && designerRepository.existsByName(dto.name())) {
            throw new ResourceAlreadyExistsException("Designer with name " + dto.name() + " already exists");
        }

        designerMapper.updateEntityFromDto(dto, entity);
        return designerMapper.toDto(entity);
    }

    @Transactional
    public void delete(Integer id) {
        if (!designerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Designer with id " + id + " not found");
        }

        designerRepository.deleteById(id);
    }
}
