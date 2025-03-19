package hr.algebra.webshop.controller;

import hr.algebra.webshop.dto.OrderDto;
import hr.algebra.webshop.dto.OrderItemDto;
import hr.algebra.webshop.model.Order;
import hr.algebra.webshop.model.OrderItem;
import hr.algebra.webshop.model.OrderStatus;
import hr.algebra.webshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @PostMapping("cart/{userId}/checkout")
    public ResponseEntity<OrderDto> completeOrder(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.completeOrder(userId));
    }
}
