package yosadchuk.needle.flow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yosadchuk.needle.flow.model.dto.CreateThreadDto;
import yosadchuk.needle.flow.model.dto.ThreadResponseDto;
import yosadchuk.needle.flow.service.ThreadService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/threads")
public class ThreadController {

    private final ThreadService threadService;

    @GetMapping
    public ResponseEntity<List<ThreadResponseDto>> findAll() {
        return ResponseEntity.ok(threadService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThreadResponseDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(threadService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ThreadResponseDto> save(@RequestBody CreateThreadDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(threadService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThreadResponseDto> update(@PathVariable Integer id, @RequestBody CreateThreadDto dto) {
        return ResponseEntity.ok(threadService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        threadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
