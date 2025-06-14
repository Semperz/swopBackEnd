package edu.badpals.swopbackend.service;

import edu.badpals.swopbackend.dto.OrderDetailDto;
import edu.badpals.swopbackend.dto.OrderDto;
import edu.badpals.swopbackend.model.Customer;
import edu.badpals.swopbackend.model.Order;
import edu.badpals.swopbackend.model.OrderDetail;
import edu.badpals.swopbackend.model.Product;
import edu.badpals.swopbackend.repository.CustomerRepository;
import edu.badpals.swopbackend.repository.OrderDetailRepository;
import edu.badpals.swopbackend.repository.OrderRepository;
import edu.badpals.swopbackend.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;
    private final AuthService authService;


    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderDetailRepository orderDetailRepository,
                        ProductRepository productRepository,
                        ModelMapper modelMapper,
                        CustomerRepository customerRepository,
                        AuthService authService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.customerRepository = customerRepository;
        this.authService = authService;
    }

    private OrderDto toDto(Order order) {
        OrderDto dto = modelMapper.map(order, OrderDto.class);
        Set<OrderDetailDto> details = order.getOrderDetails().stream()
                .map(detail -> modelMapper.map(detail, OrderDetailDto.class))
                .collect(Collectors.toSet());
        dto.setOrderDetails(details);
        return dto;
    }

    private Order toEntity(OrderDto dto) {
        Order order = modelMapper.map(dto, Order.class);
        order.setOrderDetails(null); // Evita que ModelMapper asocie detalles
        return order;
    }


    @Transactional
    public OrderDto createOrder(OrderDto orderDto, String authenticatedEmail) {
        Customer customer = customerRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Customer not found for email: " + authenticatedEmail));

        Order order = toEntity(orderDto);
        order.setCustomer(customer);

        // Guarda la orden sin detalles
        Order savedOrder = orderRepository.save(order);

        Set<OrderDetailDto> details = orderDto.getOrderDetails();
        Set<Long> productIds = details.stream()
                .map(OrderDetailDto::getProduct)
                .collect(Collectors.toSet());

        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // Crea los detalles y asígnales la orden guardada
        Set<OrderDetail> orderDetails = details.stream().map(detailDto -> {
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
        }).collect(Collectors.toSet());

        // Asocia los detalles a la orden y guarda la orden de nuevo
        savedOrder.setOrderDetails(orderDetails);
        orderRepository.save(savedOrder);

        return toDto(savedOrder);
    }


    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<OrderDto> getOrdersForCurrentUser() {
        String email = authService.getCurrentUserEmail();
        List<Order> orders = orderRepository.findByCustomerEmail(email);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
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

        Set<OrderDetail> newOrderDetails = details.stream().map(detailDto -> {
            Product product = productMap.get(detailDto.getProduct());
            if (product == null) {
                throw new RuntimeException("Product not found with ID: " + detailDto.getProduct());
            }
            return orderDetailRepository.save(new OrderDetail(
                    existingOrder,
                    product,
                    detailDto.getPrice(),
                    detailDto.getSku(),
                    detailDto.getQuantity()
            ));
        }).collect(Collectors.toSet());

        // limpiar y añadir en la orderDetails de la orden existente
        existingOrder.getOrderDetails().clear();
        existingOrder.getOrderDetails().addAll(newOrderDetails);

        existingOrder.setAmount(orderDto.getAmount());
        existingOrder.setShippingAddress(orderDto.getShippingAddress());
        existingOrder.setOrderAddress(orderDto.getOrderAddress());
        existingOrder.setOrderEmail(orderDto.getOrderEmail());
        existingOrder.setOrderStatus(orderDto.getOrderStatus());
        existingOrder.setPaymentMethod(orderDto.getPaymentMethod());
        existingOrder.setOrderDate(orderDto.getOrderDate());
        existingOrder.setFromAuction(orderDto.isFromAuction());

        orderRepository.save(existingOrder);

        return toDto(existingOrder);
    }


    @Transactional
    public void deleteOrder(Long orderId, String authenticatedEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // Verificar que el email del cliente coincida con el autenticado
        if (!order.getCustomer().getEmail().equals(authenticatedEmail)) {
            throw new RuntimeException("You are not authorized to delete this order.");
        }

        orderRepository.deleteById(orderId);
    }


    public List<String> getProductNameByOrderDetailId(Long orderDetailId) {
        OrderDetail detail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new RuntimeException("OrderDetail not found with ID: " + orderDetailId));
        return List.of(detail.getProduct().getName());
    }
}
