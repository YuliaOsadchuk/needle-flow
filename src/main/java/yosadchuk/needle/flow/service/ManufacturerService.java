package yosadchuk.needle.flow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yosadchuk.needle.flow.exception.ResourceAlreadyExistsException;
import yosadchuk.needle.flow.exception.ResourceInUseException;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.mapper.ManufacturerMapper;
import yosadchuk.needle.flow.model.dto.CreateManufacturerDto;
import yosadchuk.needle.flow.model.dto.ManufacturerResponseDto;
import yosadchuk.needle.flow.model.entity.Manufacturer;
import yosadchuk.needle.flow.repository.ManufacturerRepository;
import yosadchuk.needle.flow.repository.ThreadRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;
    private final ThreadRepository threadRepository;
    private final ManufacturerMapper manufacturerMapper;

    public List<ManufacturerResponseDto> findAll() {
        return manufacturerRepository.findAll().stream().map(manufacturerMapper::toDto).toList();
    }

    public ManufacturerResponseDto findById(Integer id) {
        return manufacturerRepository.findById(id).map(manufacturerMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer with id " + id + " not found"));
    }

    @Transactional
    public ManufacturerResponseDto save(CreateManufacturerDto dto) {
        if (manufacturerRepository.existsByName(dto.name())) {
            throw new ResourceAlreadyExistsException("Manufacturer with name " + dto.name() + " already exists");
        }
        Manufacturer savedEntity = manufacturerRepository.save(manufacturerMapper.toEntity(dto));
        return manufacturerMapper.toDto(savedEntity);
    }

    @Transactional
    public ManufacturerResponseDto update(Integer id, CreateManufacturerDto dto) {
        Manufacturer entity = manufacturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer with id " + id + " not found"));

        boolean isNameChanged = !entity.getName().equalsIgnoreCase(dto.name());
        if (isNameChanged && manufacturerRepository.existsByName(dto.name())) {
            throw new ResourceAlreadyExistsException("Manufacturer with name " + dto.name() + " already exists");
        }

        manufacturerMapper.updateEntityFromDto(dto, entity);
        return manufacturerMapper.toDto(entity);
    }

    @Transactional
    public void delete(Integer id) {
        if (!manufacturerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Manufacturer with id " + id + " not found");
        }

        if (threadRepository.existsByManufacturerId(id)) {
            throw new ResourceInUseException("Cannot delete manufacturer with id " + id + " because it has associated threads");
        }

        manufacturerRepository.deleteById(id);
    }
}
