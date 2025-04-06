package edu.badpals.swopbackend.service;

import edu.badpals.swopbackend.dto.OrderDetailDto;
import edu.badpals.swopbackend.dto.OrderDto;
import edu.badpals.swopbackend.model.Order;
import edu.badpals.swopbackend.model.OrderDetail;
import edu.badpals.swopbackend.model.Product;
import edu.badpals.swopbackend.repository.OrderDetailRepository;
import edu.badpals.swopbackend.repository.OrderRepository;
import edu.badpals.swopbackend.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderDetailRepository orderDetailRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
    }

    private OrderDto toDto(Order order) {
        Set<OrderDetailDto> details = order.getOrderDetails().stream()
                .map(detail -> new OrderDetailDto(
                        detail.getId(),
                        detail.getOrder().getId(),
                        detail.getProduct().getId(),
                        detail.getPrice(),
                        detail.getSku(),
                        detail.getQuantity()
                ))
                .collect(Collectors.toSet());
        return new OrderDto(
                order.getId(), order.getCustomer(), details ,order.getAmount(), order.getShippingAddress(),
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

        Set<OrderDetailDto> details = orderDto.getOrderDetails();

        // todos los IDs de los productos de esa Order
        Set<Long> productIds = details.stream()
                .map(OrderDetailDto::getProduct)
                .collect(Collectors.toSet());
        // se buscan los Products por ID para luego guardarlos en un Map (ID:Product)
        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        // para cada detalle de la Order se busca el Product en el Map y se crea el OrderDetail
        details.stream().map(detailDto -> {
            Product product = productMap.get(detailDto.getProduct());
            if (product == null) {
                throw new RuntimeException("Product not found with ID: " + detailDto.getProduct());
            }

            return new OrderDetail(
                    savedOrder,
                    product,
                    detailDto.getPrice(),
                    detailDto.getSku(),
                    detailDto.getQuantity()
            );
        }).forEach(orderDetailRepository::save);

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

        // Eliminar detalles antiguos
        orderDetailRepository.deleteByOrder(existingOrder);

        // Obtener nuevos detalles
        Set<OrderDetailDto> details = orderDto.getOrderDetails();

        Set<Long> productIds = details.stream()
                .map(OrderDetailDto::getProduct)
                .collect(Collectors.toSet());

        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // Crear y guardar nuevos detalles
        details.stream().map(detailDto -> {
            Product product = productMap.get(detailDto.getProduct());
            if (product == null) {
                throw new RuntimeException("Product not found with ID: " + detailDto.getProduct());
            }

            return new OrderDetail(
                    existingOrder,
                    product,
                    detailDto.getPrice(),
                    detailDto.getSku(),
                    detailDto.getQuantity()
            );
        }).forEach(orderDetailRepository::save);

        // Actualizar campos del pedido
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
