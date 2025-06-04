package edu.badpals.swopbackend.service;

import edu.badpals.swopbackend.model.*;
import edu.badpals.swopbackend.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InitDbService {

    private final CustomerRepository customerRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final BidRepository bidRepository;

    public InitDbService(CustomerRepository customerRepository,
                         ProductCategoryRepository productCategoryRepository,
                         ProductRepository productRepository,
                         CategoryRepository categoryRepository,
                         OrderRepository orderRepository,
                         OrderDetailRepository orderDetailRepository,
                         BidRepository bidRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.customerRepository = customerRepository;
        this.bidRepository = bidRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostConstruct
    public void initDatabase() {
        if (customerRepository.count() == 0) { // Si no hay clientes en la base de datos
            // Clientes
            Customer customer1 = new Customer(
                    "Sergio", passwordEncoder.encode("root"),
                    "John Doe", "123 Main St", "123 Main St", "USA", "1234567890"
            );

            Customer customer2 = new Customer(
                    "Pepe", passwordEncoder.encode("contraseña"),
                    "Jane Doe", "456 Elm St", "456 Elm St", "Canada", "0987654321"
            );

            Customer customer3 = new Customer(
                    "Ana", passwordEncoder.encode("123456"),
                    "Ana Smith", "789 Oak St", "789 Oak St", "UK", "5555555555"
            );

            customerRepository.save(customer1);
            customerRepository.save(customer2);
            customerRepository.save(customer3);

            // Categorías
            Category category1 = new Category("Action Figures", "Figuras de acción de Star Wars", "thumbnail1.jpg");
            Category category2 = new Category("LEGO", "Sets de LEGO de Star Wars", "thumbnail2.jpg");
            Category category3 = new Category("Merchandising", "Coleccionables de Star Wars", "thumbnail3.jpg");
            categoryRepository.save(category1);
            categoryRepository.save(category2);
            categoryRepository.save(category3);

            // Productos
            Product product1 = new Product("SW001", "Luke Skywalker Action Figure", new BigDecimal("19.99"), 0.5, "Figura de acción de Luke Skywalker", "thumbnail1.jpg", "image1.jpg", null, 100);
            Product product2 = new Product("SW002", "Darth Vader Action Figure", new BigDecimal("24.99"), 0.6, "Figura de acción de Darth Vader", "thumbnail2.jpg", "image2.jpg", null, 100);
            Product product3 = new Product("SW003", "Millennium Falcon LEGO Set", new BigDecimal("149.99"), 2.5, "Set de LEGO del Millennium Falcon", "thumbnail3.jpg", "image3.jpg", null, 50);
            Product product4 = new Product("SW004", "Yoda Collectible Figure", new BigDecimal("29.99"), 0.3, "Figura coleccionable de Yoda", "thumbnail4.jpg", "image4.jpg", null, 1);
            Product product5 = new Product("SW005", "Stormtrooper Action Figure", new BigDecimal("14.99"), 0.4, "Figura de acción de Stormtrooper", "thumbnail5.jpg", "image5.jpg", null, 120);
            Product product6 = new Product("SW006", "R2-D2 LEGO Set", new BigDecimal("79.99"), 1.0, "Set de LEGO de R2-D2", "thumbnail6.jpg", "image6.jpg", null, 4);
            productRepository.save(product1);
            productRepository.save(product2);
            productRepository.save(product3);
            productRepository.save(product4);
            productRepository.save(product5);
            productRepository.save(product6);

            // Relación productos-categorías
            ProductCategory productCategory1 = new ProductCategory(product1, category1);
            ProductCategory productCategory2 = new ProductCategory(product1, category3);
            ProductCategory productCategory3 = new ProductCategory(product2, category1);
            ProductCategory productCategory4 = new ProductCategory(product2, category3);
            ProductCategory productCategory5 = new ProductCategory(product3, category2);
            ProductCategory productCategory6 = new ProductCategory(product4, category3);
            ProductCategory productCategory7 = new ProductCategory(product5, category1);
            ProductCategory productCategory8 = new ProductCategory(product6, category2);
            ProductCategory productCategory9 = new ProductCategory(product6, category3);
            productCategoryRepository.save(productCategory1);
            productCategoryRepository.save(productCategory2);
            productCategoryRepository.save(productCategory3);
            productCategoryRepository.save(productCategory4);
            productCategoryRepository.save(productCategory5);
            productCategoryRepository.save(productCategory6);
            productCategoryRepository.save(productCategory7);
            productCategoryRepository.save(productCategory8);
            productCategoryRepository.save(productCategory9);

            product1.setCategories(List.of(productCategory1, productCategory2));
            product2.setCategories(List.of(productCategory3, productCategory4));
            product3.setCategories(List.of(productCategory5));
            productRepository.save(product1);
            productRepository.save(product2);
            productRepository.save(product3);

            // Pedidos para Pepe
            Order pepeOrder1 = new Order();
            pepeOrder1.setCustomer(customer2);
            pepeOrder1.setAmount(new BigDecimal("24.99"));
            pepeOrder1.setShippingAddress("456 Elm St");
            pepeOrder1.setOrderAddress("456 Elm St");
            pepeOrder1.setOrderDate(LocalDateTime.now());
            pepeOrder1.setOrderEmail("pepe@example.com");
            pepeOrder1.setPaymentMethod(PaymentMethod.PAYPAL);
            pepeOrder1.setOrderStatus(OrderStatus.PROCESSED);
            pepeOrder1.setOrderDetails(null);
            Order savedPepeOrder1 = orderRepository.save(pepeOrder1);

            OrderDetail pepeDetail1 = new OrderDetail(savedPepeOrder1, product1, product1.getPrice(), product1.getSku(), 1);
            OrderDetail pepeDetail2 = new OrderDetail(savedPepeOrder1, product3, product3.getPrice(), product3.getSku(), 1);
            orderDetailRepository.save(pepeDetail1);
            orderDetailRepository.save(pepeDetail2);

            Order pepeOrder2 = new Order();
            pepeOrder2.setCustomer(customer2);
            pepeOrder2.setAmount(new BigDecimal("49.98"));
            pepeOrder2.setShippingAddress("456 Elm St");
            pepeOrder2.setOrderAddress("456 Elm St");
            pepeOrder2.setOrderDate(LocalDateTime.now());
            pepeOrder2.setOrderEmail("pepe@example.com");
            pepeOrder2.setPaymentMethod(PaymentMethod.CREDIT_CARD);
            pepeOrder2.setOrderStatus(OrderStatus.PROCESSED);
            pepeOrder2.setOrderDetails(null);
            Order savedPepeOrder2 = orderRepository.save(pepeOrder2);

            OrderDetail pepeDetail3 = new OrderDetail(savedPepeOrder2, product2, product2.getPrice(), product2.getSku(), 2);
            orderDetailRepository.save(pepeDetail3);

            // Pedidos para Ana
            Order anaOrder1 = new Order();
            anaOrder1.setCustomer(customer3);
            anaOrder1.setAmount(new BigDecimal("174.98"));
            anaOrder1.setShippingAddress("789 Oak St");
            anaOrder1.setOrderAddress("789 Oak St");
            anaOrder1.setOrderDate(LocalDateTime.now());
            anaOrder1.setOrderEmail("ana.smith@example.com");
            anaOrder1.setPaymentMethod(PaymentMethod.CREDIT_CARD);
            anaOrder1.setOrderStatus(OrderStatus.PROCESSED);
            anaOrder1.setOrderDetails(null);
            Order savedAnaOrder1 = orderRepository.save(anaOrder1);

            OrderDetail anaDetail1 = new OrderDetail(savedAnaOrder1, product3, product3.getPrice(), product3.getSku(), 1);
            OrderDetail anaDetail2 = new OrderDetail(savedAnaOrder1, product1, product1.getPrice(), product1.getSku(), 3);
            orderDetailRepository.save(anaDetail1);
            orderDetailRepository.save(anaDetail2);

            Order anaOrder2 = new Order();
            anaOrder2.setCustomer(customer3);
            anaOrder2.setAmount(new BigDecimal("24.99"));
            anaOrder2.setShippingAddress("789 Oak St");
            anaOrder2.setOrderAddress("789 Oak St");
            anaOrder2.setOrderDate(LocalDateTime.now());
            anaOrder2.setOrderEmail("ana.smith@example.com");
            anaOrder2.setPaymentMethod(PaymentMethod.PAYPAL);
            anaOrder2.setOrderStatus(OrderStatus.PROCESSED);
            anaOrder2.setOrderDetails(null);
            Order savedAnaOrder2 = orderRepository.save(anaOrder2);

            OrderDetail anaDetail3 = new OrderDetail(savedAnaOrder2, product2, product2.getPrice(), product2.getSku(), 1);
            orderDetailRepository.save(anaDetail3);


            System.out.println("✅ Base de datos inicializada con clientes, pedidos y pujas de prueba.");
        } else {
            System.out.println("ℹ️ La base de datos ya contiene datos, no se inicializará.");
        }
    }
}

