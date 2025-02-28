package hr.algebra.webshop.repository;

import hr.algebra.webshop.model.Order;
import hr.algebra.webshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
