package yosadchuk.needle.flow.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/designs")
public class DesignController {

    private final DesignService designService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<DesignResponseDto>> findAll() {
        log.info("[Design][find all] request");
        return ResponseEntity.ok(designService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DesignResponseDto> findById(@PathVariable Integer id) {
        log.info("[Design][find by id] id: {}", id);
        DesignResponseDto responseDto = designService.findById(id);
        log.info("[Design][find by id] response: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping
    public ResponseEntity<DesignResponseDto> create(@RequestBody CreateDesignDto dto) {
        log.info("[Design][create] request: {}", dto);
        DesignResponseDto responseDto = designService.create(dto);
        log.info("[Design][create] response: {}", responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DesignResponseDto> update(@PathVariable Integer id, @RequestBody CreateDesignDto dto) {
        log.info("[Design][update] id: {}, request: {}", id, dto);
        DesignResponseDto responseDto = designService.update(id, dto);
        log.info("[Design][update] response: {}", responseDto);
        return ResponseEntity.ok().body(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("[Design][delete by id] id {}", id);
        designService.delete(id);
        log.info("[Design][delete by id] id: {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<DesignResponseDto> uploadImage(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        log.info("[Design][upload image] id: {}, filename: {}, size: {} bytes", id, file.getOriginalFilename(), file.getSize());
        String imagePath = fileStorageService.storeFile(file);
        DesignResponseDto responseDto = designService.updateImageUrl(id, imagePath);
        log.info("[Design][upload image] id: {}, saved as: {}", id, imagePath);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/shopping-list")
    public ResponseEntity<List<ShoppingListItemDto>> calculateShoppingList(@RequestBody List<Integer> designIds) {
        log.info("[Design][shopping list] designIds: {}", designIds);
        return ResponseEntity.ok().body(designService.calculateShoppingList(designIds));
    }
}
