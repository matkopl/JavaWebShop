package hr.algebra.webshop.dto;

import hr.algebra.webshop.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private Long userId;
    private List<Long> products;
    private Double totalPrice;
    private OrderStatus status;
}
