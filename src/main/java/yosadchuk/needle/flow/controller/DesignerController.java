package yosadchuk.needle.flow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yosadchuk.needle.flow.model.dto.CreateDesignerDto;
import yosadchuk.needle.flow.model.dto.DesignerResponseDto;
import yosadchuk.needle.flow.service.DesignerService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/designers")
public class DesignerController {
    private final DesignerService designerService;

    @GetMapping
    public ResponseEntity<List<DesignerResponseDto>> findAll() {
        return ResponseEntity.ok(designerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DesignerResponseDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(designerService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<DesignerResponseDto> save(@RequestBody CreateDesignerDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(designerService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DesignerResponseDto> update(@PathVariable Integer id, @RequestBody CreateDesignerDto dto) {
        return ResponseEntity.ok(designerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        designerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
