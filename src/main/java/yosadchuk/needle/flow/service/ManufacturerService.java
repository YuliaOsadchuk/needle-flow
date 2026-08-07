package yosadchuk.needle.flow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.mapper.ManufacturerMapper;
import yosadchuk.needle.flow.model.dto.CreateManufacturerDto;
import yosadchuk.needle.flow.model.dto.ManufacturerResponseDto;
import yosadchuk.needle.flow.model.entity.Manufacturer;
import yosadchuk.needle.flow.repository.ManufacturerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;
    private final ManufacturerMapper manufacturerMapper;

    public List<ManufacturerResponseDto> findAll() {
        return manufacturerRepository.findAll().stream().map(manufacturerMapper::toDto).toList();
    }

    public ManufacturerResponseDto findById(int id) {
        return manufacturerRepository.findById(id).map(manufacturerMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer with id " + id + " not found"));
    }

    @Transactional
    public ManufacturerResponseDto save(CreateManufacturerDto dto) {
        Manufacturer savedEntity = manufacturerRepository.save(manufacturerMapper.toEntity(dto));
        return manufacturerMapper.toDto(savedEntity);
    }

    @Transactional
    public ManufacturerResponseDto update(int id, CreateManufacturerDto dto) {
        Manufacturer entity = manufacturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer with id " + id + " not found"));
        manufacturerMapper.updateEntityFromDto(dto, entity);
        Manufacturer updateEntity = manufacturerRepository.save(entity);
        return manufacturerMapper.toDto(updateEntity);
    }

    @Transactional
    public void delete(int id) {
        if (!manufacturerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Manufacturer with id " + id + " not found");
        }
        manufacturerRepository.deleteById(id);
    }
}
