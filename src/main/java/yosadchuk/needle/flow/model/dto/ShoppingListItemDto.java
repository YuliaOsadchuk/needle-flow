package yosadchuk.needle.flow.model.dto;

import java.math.BigDecimal;

public record ShoppingListItemDto(Integer threadId, String code, String threadName, String manufacturerName,
                                  BigDecimal requiredQuantity, BigDecimal inStockQuantity, BigDecimal toBuyQuantity) {
}
