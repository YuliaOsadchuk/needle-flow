package yosadchuk.needle.flow.model.dto;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        Integer status,
        String message,
        LocalDateTime timestamp
) {
}
