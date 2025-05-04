package edu.badpals.swopbackend.service;

import edu.badpals.swopbackend.dto.CustomerDto;
import edu.badpals.swopbackend.model.Customer;
import edu.badpals.swopbackend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CustomerDto login(String email, String rawPassword) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(rawPassword, customer.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return new CustomerDto(
                customer.getId(),
                customer.getEmail(),
                null,
                customer.getFullName(),
                customer.getBillingAddress(),
                customer.getDefaultShippingAddress(),
                customer.getCountry(),
                customer.getPhone()
        );
    }
}

