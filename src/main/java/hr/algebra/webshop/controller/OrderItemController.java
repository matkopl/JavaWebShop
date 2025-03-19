package hr.algebra.webshop.controller;

import hr.algebra.webshop.dto.OrderItemDto;
import hr.algebra.webshop.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderItemService orderItemService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDto> getOrderItem(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.getOrderItemById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemDto> updateOrderItem(@PathVariable Long id, @RequestParam int quantity) {
        return ResponseEntity.ok(orderItemService.updateOrderItem(id, quantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        return orderItemService.deleteOrderItem(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
