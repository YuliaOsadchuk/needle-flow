package yosadchuk.needle.flow.model.dto;

import lombok.Builder;

@Builder
public record ThreadResponseDto(Integer id, String code, String name, ManufacturerResponseDto manufacturer) {
}
