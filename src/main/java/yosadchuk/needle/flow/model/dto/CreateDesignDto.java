package yosadchuk.needle.flow.model.dto;

import yosadchuk.needle.flow.model.entity.DesignStatus;

import java.util.List;

public record CreateDesignDto(String name, Integer designer, DesignStatus status,
                              List<DesignThreadRequestDto> threads) {
}
