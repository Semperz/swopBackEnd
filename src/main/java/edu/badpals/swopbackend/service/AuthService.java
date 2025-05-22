package edu.badpals.swopbackend.service;

import edu.badpals.swopbackend.config.JwtUtil;
import edu.badpals.swopbackend.model.Customer;
import edu.badpals.swopbackend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String login(String email, String rawPassword) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(rawPassword, customer.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        List<String> roles = customer.getEmail().equalsIgnoreCase("Sergio")
                ? List.of("ROLE_ADMIN")
                : List.of("ROLE_USER");

        return jwtUtil.generateToken(customer.getEmail(), roles, customer.getId());
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }


}
