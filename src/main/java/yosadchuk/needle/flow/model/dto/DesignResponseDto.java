package yosadchuk.needle.flow.model.dto;

import lombok.Builder;
import yosadchuk.needle.flow.model.entity.DesignStatus;

import java.util.List;

@Builder
public record DesignResponseDto(Integer id, String name, DesignerResponseDto designer, DesignStatus status,
                                List<DesignThreadResponseDto> threads, boolean canBeStarted) {
}
