package mate.academy.intro.dto.shoppingcart;

import java.util.Set;
import lombok.Data;
import mate.academy.intro.dto.cartitem.CartItemResponseDto;

@Data
public class ShoppingCartResponseDto {
    private Long id;
    private Long userId;
    private Set<CartItemResponseDto> cartItems;
}
