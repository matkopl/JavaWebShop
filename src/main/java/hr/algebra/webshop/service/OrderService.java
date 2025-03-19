package hr.algebra.webshop.service;

import hr.algebra.webshop.dto.OrderDto;
import hr.algebra.webshop.exceptions.ObjectEmptyException;
import hr.algebra.webshop.exceptions.EntityNotFoundException;
import hr.algebra.webshop.model.*;
import hr.algebra.webshop.repository.CartRepository;
import hr.algebra.webshop.repository.OrderRepository;
import hr.algebra.webshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toDto).toList();
    }

    public Optional<OrderDto> getOrderById(Long orderId) {
        return orderRepository.findById(orderId).map(this::toDto);
    }

    public List<OrderDto> getOrdersByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(orderRepository::findByUser)
                .orElse(List.of())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public OrderDto completeOrder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Cart with id " + userId + " not found"));

        if (cart.getItems().isEmpty()) {
            throw new ObjectEmptyException("Cart is empty. Cannot complete purchase.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = cart.getItems().entrySet().stream()
                .map(entry ->{
                    Product product = entry.getKey();
                    int quantity = entry.getValue();

                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(product);
                    orderItem.setQuantity(quantity);
                    return orderItem;
                }).toList();

        order.setOrderItems(orderItems);
        orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return toDto(order);
    }



    private OrderDto toDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getUser().getId(),
                order.getOrderItems().stream().map(item -> item.getProduct().getId()).toList(),
                order.getOrderItems().stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum(),
                order.getStatus()
        );
    }
}
