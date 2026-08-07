package yosadchuk.needle.flow.model.dto;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        int status,
        String message,
        LocalDateTime timestamp
) {
}
