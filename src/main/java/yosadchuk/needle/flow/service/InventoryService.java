package yosadchuk.needle.flow.service;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.model.dto.AddInventoryRequestDto;
import yosadchuk.needle.flow.model.entity.Inventory;
import yosadchuk.needle.flow.repository.InventoryRepository;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public void addStock(AddInventoryRequestDto dto) throws BadRequestException {
        if (dto.addBobbinMeters() == null || dto.addSkeins() == null) {
            throw new BadRequestException("One of the fields must be filled in");
        }

        Inventory inventory = inventoryRepository.findByThreadId(dto.threadId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory with thread id " + dto.threadId() + " not found"));

        inventory.setBobbinQuantity(inventory.getBobbinQuantity().add(dto.addBobbinMeters()));
        inventory.setSkeinQuantity(inventory.getSkeinQuantity() + dto.addSkeins());
    }

    @Transactional
    public void updateStock(AddInventoryRequestDto dto) throws BadRequestException {
        if (dto.addBobbinMeters() == null || dto.addSkeins() == null) {
            throw new BadRequestException("One of the fields must be filled in");
        }

        Inventory inventory = inventoryRepository.findByThreadId(dto.threadId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory with thread id " + dto.threadId() + " not found"));

        inventory.setBobbinQuantity(dto.addBobbinMeters());
        inventory.setSkeinQuantity(dto.addSkeins());
    }
}