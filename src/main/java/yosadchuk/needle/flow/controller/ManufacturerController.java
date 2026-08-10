package yosadchuk.needle.flow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yosadchuk.needle.flow.model.dto.CreateManufacturerDto;
import yosadchuk.needle.flow.model.dto.ManufacturerResponseDto;
import yosadchuk.needle.flow.service.ManufacturerService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/manufacturers")
public class ManufacturerController {
    private final ManufacturerService manufacturerService;

    @GetMapping()
    public ResponseEntity<List<ManufacturerResponseDto>> findAll() {
        return ResponseEntity.ok(manufacturerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManufacturerResponseDto> findById(@PathVariable int id) {
        return ResponseEntity.ok(manufacturerService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<ManufacturerResponseDto> save(@RequestBody CreateManufacturerDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(manufacturerService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ManufacturerResponseDto> update(@PathVariable int id, @RequestBody CreateManufacturerDto dto) {
        return ResponseEntity.ok(manufacturerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        manufacturerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
