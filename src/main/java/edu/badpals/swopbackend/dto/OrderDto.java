package edu.badpals.swopbackend.dto;

import edu.badpals.swopbackend.model.Customer;
import edu.badpals.swopbackend.model.OrderStatus;
import edu.badpals.swopbackend.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private Customer customer;
    private BigDecimal amount;
    private String shippingAddress;
    private String orderAddress;
    private String orderEmail;
    private OrderStatus orderStatus;
    private PaymentMethod paymentMethod;
    private LocalDateTime orderDate;
}