package yosadchuk.needle.flow.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yosadchuk.needle.flow.exception.ResourceNotFoundException;
import yosadchuk.needle.flow.model.dto.CreateDesignDto;
import yosadchuk.needle.flow.model.dto.DesignResponseDto;
import yosadchuk.needle.flow.model.dto.DesignThreadRequestDto;
import yosadchuk.needle.flow.model.dto.DesignThreadResponseDto;
import yosadchuk.needle.flow.model.entity.Design;
import yosadchuk.needle.flow.model.entity.DesignThread;
import yosadchuk.needle.flow.model.entity.Inventory;
import yosadchuk.needle.flow.model.entity.Thread;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DesignMapper {

    private final DesignerMapper designerMapper;

    public DesignResponseDto toDto(Design entity) {
        if (entity == null) return null;

        List<DesignThreadResponseDto> threadDto = entity.getThreads() == null ? Collections.emptyList()
                : entity.getThreads()
                .stream()
                .map(this::toDesignThreadDto)
                .toList();

        boolean canBeStarted = !threadDto.isEmpty() && threadDto.stream().allMatch(DesignThreadResponseDto::isSufficient);

        return DesignResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .designer(designerMapper.toDto(entity.getDesigner()))
                .status(entity.getStatus())
                .threads(threadDto)
                .canBeStarted(canBeStarted)
                .build();
    }

    public Design toEntity(CreateDesignDto dto) {
        if (dto == null) return null;
        Design entity = new Design();
        entity.setName(dto.name());
        entity.setStatus(dto.status());
        return entity;
    }

    public void updateEntityFromDto(CreateDesignDto dto, Design entity, Map<Integer, Thread> threadsMap) {
        entity.setName(dto.name());
        entity.setStatus(dto.status());
        if (dto.threads() != null) {
            updateDesignThreads(dto.threads(), entity, threadsMap);
        }
    }

    private void updateDesignThreads(List<DesignThreadRequestDto> dtos, Design entity, Map<Integer, Thread> threadsMap) {
        Set<Integer> incomingIds = dtos.stream()
                .map(DesignThreadRequestDto::threadId)
                .collect(Collectors.toSet());

        entity.getThreads().removeIf(dt -> !incomingIds.contains(dt.getThread().getId()));

        for (DesignThreadRequestDto dto : dtos) {
            Optional<DesignThread> existing = entity.getThreads().stream()
                    .filter(dt -> dt.getThread().getId().equals(dto.threadId()))
                    .findFirst();

            if (existing.isPresent()) {
                existing.get().setRequiredMeters(dto.requiredMeters());
            } else {
                Thread thread = threadsMap.get(dto.threadId());
                if (thread == null) {
                    throw new ResourceNotFoundException("Thread with id " + dto.threadId() + " not found");
                }

                DesignThread dt = new DesignThread();
                dt.setDesign(entity);
                dt.setThread(thread);
                dt.setRequiredMeters(dto.requiredMeters());

                entity.getThreads().add(dt);
            }
        }
    }

    private DesignThreadResponseDto toDesignThreadDto(DesignThread designThread) {
        if (designThread == null) return null;
        BigDecimal requiredThread = designThread.getRequiredMeters() != null ? designThread.getRequiredMeters() : BigDecimal.ZERO;

        Inventory inventory = designThread.getThread().getInventory();
        BigDecimal availableMeters = BigDecimal.ZERO;
        if (inventory != null) {
            BigDecimal availableSkeinsInMeters = new BigDecimal(inventory.getSkeinQuantity() * 8);
            availableMeters = inventory.getBobbinQuantity().add(availableSkeinsInMeters);
        }

        boolean isSufficient =  availableMeters.compareTo(requiredThread) >=0;

        return new DesignThreadResponseDto(
                designThread.getId(),
                designThread.getThread().getId(),
                designThread.getThread().getCode(),
                designThread.getThread().getName(),
                designThread.getRequiredMeters(),
                availableMeters,
                isSufficient
        );
    }
}
