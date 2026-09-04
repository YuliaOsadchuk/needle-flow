package yosadchuk.needle.flow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yosadchuk.needle.flow.model.dto.CreateManufacturerDto;
import yosadchuk.needle.flow.model.dto.ManufacturerResponseDto;
import yosadchuk.needle.flow.service.ManufacturerService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/manufacturers")
public class ManufacturerController {
    private final ManufacturerService manufacturerService;

    @GetMapping()
    public ResponseEntity<List<ManufacturerResponseDto>> findAll() {
        log.info("[Manufacturer][find all] request");
        return ResponseEntity.ok(manufacturerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManufacturerResponseDto> findById(@PathVariable Integer id) {
        log.info("[Manufacturer][find by id] id: {}", id);
        ManufacturerResponseDto responseDto = manufacturerService.findById(id);
        log.info("[Manufacturer][find by id] response: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping()
    public ResponseEntity<ManufacturerResponseDto> create(@Valid @RequestBody CreateManufacturerDto dto) {
        log.info("[Manufacturer][create] request: {}", dto);
        ManufacturerResponseDto responseDto = manufacturerService.create(dto);
        log.info("[Manufacturer][create] response: {}", responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ManufacturerResponseDto> update(@PathVariable Integer id, @Valid  @RequestBody CreateManufacturerDto dto) {
        log.info("[Manufacturer][update] id: {}, request: {}", id, dto);
        ManufacturerResponseDto responseDto = manufacturerService.update(id, dto);
        log.info("[Manufacturer][update] response: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("[Manufacturer][delete by id] id {}", id);
        manufacturerService.delete(id);
        log.info("[Manufacturer][delete by id] id: {} deleted", id);
        return ResponseEntity.noContent().build();
    }
}
