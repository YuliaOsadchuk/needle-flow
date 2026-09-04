package yosadchuk.needle.flow.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yosadchuk.needle.flow.model.dto.*;
import yosadchuk.needle.flow.service.ThreadService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/threads")
public class ThreadController {

    private final ThreadService threadService;

    @GetMapping("/options")
    public ResponseEntity<List<ThreadResponseDto>> findAll() {
        log.info("[Thread][find all options] request");
        List<ThreadResponseDto> response = threadService.findAll();
        log.info("[Thread][find all options] count: {}", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<ThreadResponseDto>> findAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer manufacturerId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        log.info("[Thread][find all] page: {}, size: {}, search: {}, manufacturerId: {} ", page, size, search, manufacturerId);
        return ResponseEntity.ok(threadService.findAllPaged(pageable, search, manufacturerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThreadResponseDto> findById(@PathVariable Integer id) {
        log.info("[Thread][find by id] id: {}", id);
        ThreadResponseDto responseDto = threadService.findById(id);
        log.info("[Thread][find by id] response: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping
    public ResponseEntity<ThreadResponseDto> save(@RequestBody CreateThreadDto dto) {
        log.info("[Thread][create] request: {}", dto);
        ThreadResponseDto responseDto = threadService.create(dto);
        log.info("[Thread][create] response: {}", responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThreadResponseDto> update(@PathVariable Integer id, @RequestBody CreateThreadDto dto) {
        log.info("[Thread][update] id: {}, request: {}", id, dto);
        ThreadResponseDto responseDto = threadService.update(id, dto);
        log.info("[Thread][update] response: {}", responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("[Thread][delete by id] id {}", id);
        threadService.delete(id);
        log.info("[Thread][delete by id] id: {} deleted", id);
        return ResponseEntity.noContent().build();
    }
}
