package item.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import item.model.GetCartQuery;
import item.model.GetCartResult;
import item.model.UpdateCartQuantityCommand;
import item.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public GetCartResult getCart(@ParameterObject GetCartQuery query) {
        return cartService.getCart(query);
    }

    @PostMapping("/quantity")
    public void updateQuantity(@RequestBody UpdateCartQuantityCommand command) {
        cartService.updateQuantity(command);
    }
}

