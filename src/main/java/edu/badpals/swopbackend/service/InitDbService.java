package edu.badpals.swopbackend.service;

import edu.badpals.swopbackend.model.Customer;
import edu.badpals.swopbackend.repository.CustomerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class InitDbService {

    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public InitDbService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostConstruct
    public void initDatabase() {
        if (customerRepository.count() == 0) { // Si no hay clientes en la base de datos
            Customer customer1 = new Customer(
                    "john.doe@example.com", passwordEncoder.encode("password123"),
                    "John Doe", "123 Main St", "123 Main St", "USA", "1234567890"
            );

            Customer customer2 = new Customer(
                    "jane.doe@example.com", passwordEncoder.encode("password456"),
                    "Jane Doe", "456 Elm St", "456 Elm St", "Canada", "0987654321"
            );

            customerRepository.save(customer1);
            customerRepository.save(customer2);

            System.out.println("✅ Base de datos inicializada con clientes de prueba.");
        } else {
            System.out.println("ℹ️ La base de datos ya contiene datos, no se inicializará.");
        }
    }
}

