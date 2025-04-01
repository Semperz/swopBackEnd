package edu.badpals.swopbackend.service;

import edu.badpals.swopbackend.dto.OrderDto;
import edu.badpals.swopbackend.model.Order;
import edu.badpals.swopbackend.repository.CustomerRepository;
import edu.badpals.swopbackend.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    private OrderDto toDto(Order order) {
        return new OrderDto(
                order.getId(), order.getCustomer(), order.getAmount(), order.getShippingAddress(),
                order.getOrderAddress(), order.getOrderEmail(), order.getOrderStatus(), order.getPaymentMethod(),
                order.getOrderDate()
        );
    }

    private Order toEntity(OrderDto dto) {
        Order order = new Order();
        order.setCustomer(dto.getCustomer());
        order.setAmount(dto.getAmount());
        order.setShippingAddress(dto.getShippingAddress());
        order.setOrderAddress(dto.getOrderAddress());
        order.setOrderEmail(dto.getOrderEmail());
        order.setOrderStatus(dto.getOrderStatus());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setOrderDate(dto.getOrderDate());
        return order;
    }

    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        Order order = toEntity(orderDto);
        Order savedOrder = orderRepository.save(order);
        return toDto(savedOrder);
    }

    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::toDto).collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        return toDto(order);
    }

    @Transactional
    public OrderDto updateOrder(Long id, OrderDto orderDto) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        existingOrder.setCustomer(orderDto.getCustomer());
        existingOrder.setAmount(orderDto.getAmount());
        existingOrder.setShippingAddress(orderDto.getShippingAddress());
        existingOrder.setOrderAddress(orderDto.getOrderAddress());
        existingOrder.setOrderEmail(orderDto.getOrderEmail());
        existingOrder.setOrderStatus(orderDto.getOrderStatus());
        existingOrder.setPaymentMethod(orderDto.getPaymentMethod());
        existingOrder.setOrderDate(orderDto.getOrderDate());
        return toDto(existingOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with ID: " + id);
        }
        orderRepository.deleteById(id);
    }




}
