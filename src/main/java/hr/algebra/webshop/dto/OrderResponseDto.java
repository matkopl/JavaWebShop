package hr.algebra.webshop.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderResponseDto {
    private long orderId;
    private String username;
    private List<String> products;
    private Double totalPrice;
}
