package yosadchuk.needle.flow.model.dto;

import yosadchuk.needle.flow.model.entity.DesignStatus;

public record CreateDesignDto(String name, Integer designer, DesignStatus status) {
}
