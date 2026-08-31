package yosadchuk.needle.flow.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yosadchuk.needle.flow.exception.ResourceAlreadyExistsException;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.mapper.DesignMapper;
import yosadchuk.needle.flow.model.dto.CreateDesignDto;
import yosadchuk.needle.flow.model.dto.DesignResponseDto;
import yosadchuk.needle.flow.model.dto.DesignThreadRequestDto;
import yosadchuk.needle.flow.model.dto.ShoppingListItemDto;
import yosadchuk.needle.flow.model.entity.*;
import yosadchuk.needle.flow.model.entity.Thread;
import yosadchuk.needle.flow.repository.DesignRepository;
import yosadchuk.needle.flow.repository.DesignThreadRepository;
import yosadchuk.needle.flow.repository.DesignerRepository;
import yosadchuk.needle.flow.repository.ThreadRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DesignService {

    private final DesignRepository designRepository;
    private final DesignerRepository designerRepository;
    private final ThreadRepository threadRepository;
    private final DesignThreadRepository designThreadRepository;
    private final DesignMapper designMapper;

    public List<DesignResponseDto> findAll() {
        return designRepository.findAllWithDetails().stream().map(designMapper::toDto).toList();
    }

    public DesignResponseDto findById(Integer id) {
        return designRepository.findByIdWithDetails(id).map(designMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Design with id " + id + " not found"));
    }

    @Transactional
    public DesignResponseDto create(CreateDesignDto dto) {
        if (designRepository.existsByNameAndDesignerId(dto.name(), dto.designer())) {
            throw new ResourceAlreadyExistsException("Design with name " + dto.name() + " for designer already exists");
        }

        Designer designer = designerRepository.findById(dto.designer())
                .orElseThrow(() -> new ResourceNotFoundException("Designer with id " + dto.designer() + " not found"));
        Design entity = designMapper.toEntity(dto);
        entity.setDesigner(designer);

        if (entity.getThreads() == null) {
            entity.setThreads(new ArrayList<>());
        }

        if (dto.threads() != null && !dto.threads().isEmpty()) {
            List<DesignThread> designThreads = getDesignThreadsList(dto, entity);
            entity.getThreads().addAll(designThreads);
        }

        return designMapper.toDto(designRepository.save(entity));
    }

    @Transactional
    public DesignResponseDto update(Integer id, CreateDesignDto dto) {
        Design entity = designRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Design with id " + id + " not found"));

        boolean isNameOrDesignerChanged = !entity.getName().equals(dto.name())
                || !entity.getDesigner().getId().equals(dto.designer());

        if (isNameOrDesignerChanged && designRepository.existsByNameAndDesignerId(dto.name(), dto.designer())) {
            throw new ResourceAlreadyExistsException("Design with name " + dto.name() + " for designer already exists");
        }

        List<Integer> threadsIds = dto.threads().stream()
                .map(DesignThreadRequestDto::threadId)
                .toList();
        Map<Integer, Thread> threadsMap = threadRepository.findAllById(threadsIds).stream()
                .collect(Collectors.toMap(Thread::getId, t -> t));

        designMapper.updateEntityFromDto(dto, entity, threadsMap);
        if (!entity.getDesigner().getId().equals(dto.designer())) {
            Designer designer = designerRepository.findById(dto.designer())
                    .orElseThrow(() -> new ResourceNotFoundException("Designer with id " + dto.designer() + " not found"));
            entity.setDesigner(designer);
        }
        return designMapper.toDto(entity);
    }

    @Transactional
    public void delete(Integer id) {
        Design design = designRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Design with id " + id + " not found"));

        if (design.getImageUrl() != null) {
            deleteImageFile(design.getImageUrl());
        }

        designRepository.deleteById(id);
    }

    @Transactional
    public DesignResponseDto updateImageUrl(Integer id, String imagePath) {
        Design entity = designRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Design with id " + id + " not found"));

        entity.setImageUrl(imagePath);
        return designMapper.toDto(entity);
    }

    public List<ShoppingListItemDto> calculateShoppingList(List<Integer> designIds) {
        if (designIds == null || designIds.isEmpty()) {
            return List.of();
        }

        List<DesignThread> designThreads = designThreadRepository.findByDesignIdIn(designIds);
        Map<Thread, BigDecimal> totalRequiredMap = designThreads.stream()
                .collect(Collectors.groupingBy(DesignThread::getThread,
                        Collectors.reducing(BigDecimal.ZERO, DesignThread::getRequiredMeters, BigDecimal::add)));

        return totalRequiredMap.entrySet().stream()
                .map(entry -> {
                    Thread thread = entry.getKey();
                    Inventory inventory = thread.getInventory();
                    BigDecimal inStock = inventory.getBobbinQuantity().add(BigDecimal.valueOf(inventory.getSkeinQuantity() * 8));
                    BigDecimal required = entry.getValue();
                    BigDecimal toBuy = required.subtract(inStock);

                    return new ShoppingListItemDto(
                            thread.getId(),
                            thread.getCode(),
                            thread.getName(),
                            thread.getManufacturer().getName(),
                            required,
                            inStock,
                            toBuy
                    );
                })
                .sorted(Comparator.comparing(ShoppingListItemDto::manufacturerName).thenComparing(ShoppingListItemDto::code))
                .toList();
    }

    private void deleteImageFile(String imagePath) {
        try {
            String fileName = Paths.get(imagePath).getFileName().toString();
            Path filePath = Paths.get("uploads", "designs", fileName).toAbsolutePath().normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Не вдалося видалити файл зображення за шляхом: {}", imagePath, e);
        }
    }

    private List<DesignThread> getDesignThreadsList(CreateDesignDto dto, Design entity) {
        return dto.threads().stream().map(threadDto -> {
            Thread thread = threadRepository.findById(threadDto.threadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Thread with id " + threadDto.threadId() + " not found"));

            DesignThread designThread = new DesignThread();
            designThread.setDesign(entity);
            designThread.setThread(thread);
            designThread.setRequiredMeters(threadDto.requiredMeters());
            return designThread;
        }).toList();
    }
}
