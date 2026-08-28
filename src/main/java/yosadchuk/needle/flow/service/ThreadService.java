package yosadchuk.needle.flow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yosadchuk.needle.flow.exception.ResourceAlreadyExistsException;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.mapper.ThreadMapper;
import yosadchuk.needle.flow.model.dto.CreateThreadDto;
import yosadchuk.needle.flow.model.dto.ThreadResponseDto;
import yosadchuk.needle.flow.model.entity.Inventory;
import yosadchuk.needle.flow.model.entity.Manufacturer;
import yosadchuk.needle.flow.model.entity.Thread;
import yosadchuk.needle.flow.repository.InventoryRepository;
import yosadchuk.needle.flow.repository.ManufacturerRepository;
import yosadchuk.needle.flow.repository.ThreadRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThreadService {
    private final ThreadRepository threadRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final InventoryRepository inventoryRepository;
    private final ThreadMapper mapper;

    public List<ThreadResponseDto> findAll() {
        return threadRepository.findAll().stream().map(mapper::toDto).toList();
    }

    public ThreadResponseDto findById(Integer id) {
        return threadRepository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Thread with id " + id + " not found"));
    }

    @Transactional
    public ThreadResponseDto create(CreateThreadDto dto) {
        if (threadRepository.existsByCodeAndManufacturerId(dto.code(), dto.manufacturerId())) {
            throw new ResourceAlreadyExistsException("Thread with code " + dto.code() + " already exists for this manufacturer");
        }

        Manufacturer manufacturer = manufacturerRepository.findById(dto.manufacturerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer with id " + dto.manufacturerId() + " not found"));

        Thread entity = mapper.toEntity(dto);
        entity.setManufacturer(manufacturer);
        threadRepository.save(entity);

        Inventory inventory = new Inventory();
        inventory.setThread(entity);
        inventory.setBobbinQuantity(BigDecimal.ZERO);
        inventory.setSkeinQuantity(0);

        inventoryRepository.save(inventory);
        entity.setInventory(inventory);

        return mapper.toDto(entity);
    }

    @Transactional
    public ThreadResponseDto update(Integer id, CreateThreadDto dto) {
        Thread entity = threadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thread with id " + id + " not found"));

        boolean isCodeOrManufacturerChanged = !entity.getCode().equals(dto.code())
                || !entity.getManufacturer().getId().equals(dto.manufacturerId());

        if (isCodeOrManufacturerChanged && threadRepository.existsByCodeAndManufacturerId(dto.code(), dto.manufacturerId())) {
            throw new ResourceAlreadyExistsException("Thread with code " + dto.code() + " already exists for this manufacturer");
        }

        mapper.updateEntityFromDto(dto, entity);

        if (!entity.getManufacturer().getId().equals(dto.manufacturerId())) {
            Manufacturer manufacturer = manufacturerRepository.findById(dto.manufacturerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manufacturer with id " + dto.manufacturerId() + " not found"));
            entity.setManufacturer(manufacturer);
        }
        return mapper.toDto(entity);
    }

    @Transactional
    public void delete(Integer id) {
        if (!threadRepository.existsById(id)) {
            throw new ResourceNotFoundException("Thread with id " + id + " not found");
        }
        threadRepository.deleteById(id);
    }
}
