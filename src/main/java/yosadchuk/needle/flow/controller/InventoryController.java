package yosadchuk.needle.flow.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yosadchuk.needle.flow.model.dto.AddInventoryRequestDto;
import yosadchuk.needle.flow.service.InventoryService;

@Slf4j
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/add")
    public ResponseEntity<Void> addStock(@RequestBody AddInventoryRequestDto dto) {
        log.info("[Inventory][add stock] dto: {}", dto);
        inventoryService.addStock(dto);
        log.info("[Inventory][add stock] successfully added to stock: {}", dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateStock(@RequestBody AddInventoryRequestDto dto) {
        log.info("[Inventory][update stock] dto: {}", dto);
        inventoryService.updateStock(dto);
        log.info("[Inventory][update stock] successfully updated stock: {}", dto);
        return ResponseEntity.noContent().build();
    }
}
