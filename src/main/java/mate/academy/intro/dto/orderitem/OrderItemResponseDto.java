package mate.academy.intro.dto.orderitem;

import lombok.Data;

@Data
public class OrderItemResponseDto {
    private Long id;
    private String bookId;
    private int quantity;
}
