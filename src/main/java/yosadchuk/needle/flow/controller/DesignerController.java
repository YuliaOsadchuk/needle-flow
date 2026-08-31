package yosadchuk.needle.flow.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yosadchuk.needle.flow.model.dto.CreateDesignerDto;
import yosadchuk.needle.flow.model.dto.DesignerResponseDto;
import yosadchuk.needle.flow.service.DesignerService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/designers")
public class DesignerController {
    private final DesignerService designerService;

    @GetMapping
    public ResponseEntity<List<DesignerResponseDto>> findAll() {
        log.info("[Designer][find all] request");
        return ResponseEntity.ok(designerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DesignerResponseDto> findById(@PathVariable Integer id) {
        log.info("[Designer][find by id]  id: {}", id);
        DesignerResponseDto responseDto = designerService.findById(id);
        log.info("[Designer][find by id] response: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping()
    public ResponseEntity<DesignerResponseDto> create(@RequestBody CreateDesignerDto dto) {
        log.info("[Designer][create] request: {}", dto);
        DesignerResponseDto responseDto = designerService.create(dto);
        log.info("[Designer][create] response: {}", responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DesignerResponseDto> update(@PathVariable Integer id, @RequestBody CreateDesignerDto dto) {
        log.info("[Designer][update] id: {}, request: {}", id, dto);
        DesignerResponseDto responseDto = designerService.update(id, dto);
        log.info("[Designer][update] response: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("[Designer][delete by id] id {}", id);
        designerService.delete(id);
        log.info("[Designer][delete by id] id: {} deleted", id);
        return ResponseEntity.noContent().build();
    }
}
