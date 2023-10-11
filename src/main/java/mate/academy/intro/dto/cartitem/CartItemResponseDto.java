package mate.academy.intro.dto.cartitem;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CartItemResponseDto {
    private Long id;
    private String bookId;
    private String bookTitle;
    private int quantity;
}
