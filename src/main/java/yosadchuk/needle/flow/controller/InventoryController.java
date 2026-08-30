package yosadchuk.needle.flow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yosadchuk.needle.flow.model.dto.AddInventoryRequestDto;
import yosadchuk.needle.flow.service.InventoryService;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/add")
    public ResponseEntity<Void> addStock(@RequestBody AddInventoryRequestDto dto) {
        inventoryService.addStock(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateStock(@RequestBody AddInventoryRequestDto dto) {
        inventoryService.updateStock(dto);
        return ResponseEntity.noContent().build();
    }
}
