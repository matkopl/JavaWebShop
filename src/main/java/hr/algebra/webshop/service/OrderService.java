package hr.algebra.webshop.service;

import hr.algebra.webshop.dto.OrderDto;
import hr.algebra.webshop.model.*;
import hr.algebra.webshop.repository.OrderRepository;
import hr.algebra.webshop.repository.ProductRepository;
import hr.algebra.webshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> new OrderDto(
                        order.getId(),
                        order.getUser().getId(),
                        order.getOrderItems().stream().map(item -> item.getProduct().getId()).toList(),
                        order.getOrderItems().stream().mapToDouble(item -> item.getProduct().getPrice()).sum(),
                        order.getStatus()))
                .toList();
    }

    public Optional<OrderDto> getOrderById(Long orderId) {
        return orderRepository.findById(orderId).map(order -> new OrderDto(
                order.getId(),
                order.getUser().getId(),
                order.getOrderItems().stream().map(item -> item.getProduct().getId()).toList(),
                order.getOrderItems().stream().mapToDouble(item -> item.getProduct().getPrice()).sum(),
                order.getStatus()));
    }

    public List<OrderDto> getOrdersByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return orderRepository.findByUser(user.get()).stream()
                    .map(order -> new OrderDto(
                            order.getId(),
                            order.getUser().getId(),
                            order.getOrderItems().stream().map(item -> item.getProduct().getId()).toList(),
                            order.getOrderItems().stream().mapToDouble(item -> item.getProduct().getPrice()).sum(),
                            order.getStatus()))
                    .toList();
        }
        return List.of();
    }

    public Optional<OrderDto> createOrder(OrderDto orderDto) {
        Optional<User> user = userRepository.findById(orderDto.getUserId());
        if (user.isPresent()) {
            return Optional.empty();
        }

        List<Product> products = productRepository.findAllById(orderDto.getProducts());
        if (products.isEmpty()) {
            return Optional.empty();
        }

        double totalPrice = products.stream().mapToDouble(Product::getPrice).sum();

        Order order = new Order();
        order.setUser(user.get());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = products.stream()
                .map(product -> new OrderItem(null, order, product, 1))
                .toList();

        order.setOrderItems(orderItems);
        orderRepository.save(order);

        return Optional.of(new OrderDto(
                order.getId(),
                order.getUser().getId(),
                orderItems.stream().map(item -> item.getProduct().getId()).toList(),
                totalPrice,
                order.getStatus()
        ));
    }

    public Optional<OrderDto> updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus(orderStatus);
            orderRepository.save(order);

            return Optional.of(new OrderDto(
                    order.getId(),
                    order.getUser().getId(),
                    order.getOrderItems().stream().map(item -> item.getProduct().getId()).toList(),
                    order.getOrderItems().stream().mapToDouble(item -> item.getProduct().getPrice()).sum(),
                    order.getStatus()));
        }
        return Optional.empty();
    }

    public boolean deleteOrder(Long orderId) {
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
            return true;
        }
        return false;
    }
}
