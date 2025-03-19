package hr.algebra.webshop.service;

import hr.algebra.webshop.dto.OrderItemDto;
import hr.algebra.webshop.exceptions.EntityNotFoundException;
import hr.algebra.webshop.exceptions.ObjectEmptyException;
import hr.algebra.webshop.model.Order;
import hr.algebra.webshop.model.OrderItem;
import hr.algebra.webshop.model.Product;
import hr.algebra.webshop.repository.OrderItemRepository;
import hr.algebra.webshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addOrUpdateOrderItem(Order order, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + productId + " not found"));

        if (product.getQuantity() < quantity) {
            throw new ObjectEmptyException("Insufficient quantity for product with id " + productId);
        }


        OrderItem orderItem = order.getOrderItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseGet(() -> createNewOrderItem(order, product));

        orderItem.setQuantity(orderItem.getQuantity() + quantity);
        orderItemRepository.save(orderItem);

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
    }

    private OrderItem createNewOrderItem(Order order, Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(0);
        return orderItem;
    }

    public OrderItemDto getOrderItemById(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem with id " + id + " not found"));

        return toDto(orderItem);
    }

    @Transactional
    public OrderItemDto updateOrderItem(Long id, int quantity) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem with id " + id + " not found"));

        int difference = quantity - orderItem.getQuantity();
        Product product = orderItem.getProduct();

        if (product.getQuantity() < difference) {
            throw new ObjectEmptyException("Insufficient quantity for product with id " + product.getId());
        }

        product.setQuantity(product.getQuantity() - difference);
        productRepository.save(product);

        orderItem.setQuantity(quantity);
        orderItemRepository.save(orderItem);

        return toDto(orderItem);
    }

    @Transactional
    public boolean deleteOrderItem(Long id) {
        if (orderItemRepository.existsById(id)) {
            OrderItem orderItem = orderItemRepository.findById(id).get();
            Product product = orderItem.getProduct();

            product.setQuantity(product.getQuantity() + orderItem.getQuantity());
            productRepository.save(product);

            orderItemRepository.delete(orderItem);
            return true;
        }
        return false;
    }

    private OrderItemDto toDto(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getId(),
                orderItem.getOrder().getId(),
                orderItem.getProduct().getId(),
                orderItem.getQuantity()
        );
    }
}
