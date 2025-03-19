package hr.algebra.webshop.repository;

import hr.algebra.webshop.model.Order;
import hr.algebra.webshop.model.OrderStatus;
import hr.algebra.webshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    Optional<Order> findByUserAndStatus(User user, OrderStatus status);
}
