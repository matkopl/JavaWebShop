package hr.algebra.webshop.service;


import hr.algebra.webshop.dto.CartDto;
import hr.algebra.webshop.exceptions.EntityNotFoundException;
import hr.algebra.webshop.model.Cart;
import hr.algebra.webshop.model.Product;
import hr.algebra.webshop.model.User;
import hr.algebra.webshop.repository.CartRepository;
import hr.algebra.webshop.repository.ProductRepository;
import hr.algebra.webshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public Optional<CartDto> getCartByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createCartForUser(user));

        return Optional.of(toDto(cart));
    }

    public CartDto addProductToCart(Long productId, Long userId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + productId + " not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> createCartForUser(user));

        cart.getItems().put(product, cart.getItems().getOrDefault(product, 0) + quantity);
        return toDto(cartRepository.save(cart));
    }

    public Optional<CartDto> removeProductFromCart(Long productId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + productId + " not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Cart with id " + userId + " not found"));

        cart.getItems().remove(product);
        return Optional.of(toDto(cartRepository.save(cart)));
    }

    public boolean clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        Optional<Cart> optionalCart = cartRepository.findByUser(user);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cart.getItems().clear();
            cartRepository.save(cart);
            return true;
        }
        return false;
    }

    private Cart createCartForUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new HashMap<>());
        return cartRepository.save(cart);
    }

    private CartDto toDto(Cart cart) {
        Map<Long, Integer> itemsDto = new HashMap<>();
        for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
            itemsDto.put(entry.getKey().getId(), entry.getValue());
        }
        return new CartDto(cart.getId(), cart.getUser().getId(), itemsDto);
    }

}
