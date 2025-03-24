package edu.badpals.swopbackend.dto;

import edu.badpals.swopbackend.model.Customer;
import edu.badpals.swopbackend.model.OrderStatus;
import edu.badpals.swopbackend.model.PaymentMethod;


import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public OrderDto() {
    }

    public OrderDto(Long id, Customer customer, BigDecimal amount, String shippingAddress, String orderAddress, String orderEmail, OrderStatus orderStatus, PaymentMethod paymentMethod, LocalDateTime orderDate) {
        this.id = id;
        this.customer = customer;
        this.amount = amount;
        this.shippingAddress = shippingAddress;
        this.orderAddress = orderAddress;
        this.orderEmail = orderEmail;
        this.orderStatus = orderStatus;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getOrderEmail() {
        return orderEmail;
    }

    public void setOrderEmail(String orderEmail) {
        this.orderEmail = orderEmail;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
}