package yosadchuk.needle.flow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yosadchuk.needle.flow.model.dto.CreateDesignDto;
import yosadchuk.needle.flow.model.dto.DesignResponseDto;
import yosadchuk.needle.flow.model.dto.ShoppingListItemDto;
import yosadchuk.needle.flow.service.DesignService;
import yosadchuk.needle.flow.service.FileStorageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/designs")
public class DesignController {

    private final DesignService designService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<DesignResponseDto>> findAll() {
        return ResponseEntity.ok(designService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DesignResponseDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(designService.findById(id));
    }

    @PostMapping
    public ResponseEntity<DesignResponseDto> save(@RequestBody CreateDesignDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(designService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DesignResponseDto> update(@PathVariable Integer id, @RequestBody CreateDesignDto dto) {
        return ResponseEntity.ok().body(designService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        designService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<DesignResponseDto> uploadImage(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        String imagePath = fileStorageService.storeFile(file);
        return ResponseEntity.ok(designService.updateImageUrl(id, imagePath));
    }

    @PostMapping("/shopping-list")
    public ResponseEntity<List<ShoppingListItemDto>> calculateShoppingList(@RequestBody List<Integer> designIds) {
        return ResponseEntity.ok().body(designService.calculateShoppingList(designIds));
    }
}
