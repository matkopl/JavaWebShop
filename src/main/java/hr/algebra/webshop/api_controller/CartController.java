package hr.algebra.webshop.api_controller;

import hr.algebra.webshop.dto.CartDto;
import hr.algebra.webshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCart(@PathVariable Long userId) {
        return cartService.getCartByUserId(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/add/{productId}")
    public ResponseEntity<CartDto> addProductToCart(@PathVariable Long userId, @PathVariable Long productId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.addProductToCart(userId, productId, quantity));
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<CartDto> removeProductFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        return cartService.removeProductFromCart(userId, productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<CartDto> clearCart(@PathVariable Long userId) {
        if (cartService.clearCart(userId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
