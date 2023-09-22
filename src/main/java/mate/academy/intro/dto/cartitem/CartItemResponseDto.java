package mate.academy.intro.dto.cartitem;

import lombok.Data;

@Data
public class CartItemResponseDto {
    private Long id;
    private String bookId;
    private String bookTitle;
    private int quantity;
}
