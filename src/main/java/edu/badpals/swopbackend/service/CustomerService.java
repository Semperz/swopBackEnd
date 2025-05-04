package edu.badpals.swopbackend.service;

import edu.badpals.swopbackend.dto.CustomerDto;
import edu.badpals.swopbackend.model.Customer;
import edu.badpals.swopbackend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private CustomerDto toDto(Customer customer) {
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

    private Customer toEntity(CustomerDto dto) {
        Customer customer = new Customer();
        customer.setEmail(dto.getEmail());
        customer.setPassword(passwordEncoder.encode(dto.getPassword())); // Encripta contraseña
        customer.setFullName(dto.getFullName());
        customer.setBillingAddress(dto.getBillingAddress());
        customer.setDefaultShippingAddress(dto.getDefaultShippingAddress());
        customer.setCountry(dto.getCountry());
        customer.setPhone(dto.getPhone());
        return customer;
    }

    @Transactional
    public CustomerDto createCustomer(CustomerDto customerDto) {
        Customer customer = toEntity(customerDto);
        Customer savedCustomer = customerRepository.save(customer);
        return toDto(savedCustomer);
    }

    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        return toDto(customer);
    }

    @Transactional
    public CustomerDto updateCustomer(Long id, CustomerDto customerDto) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));

        existingCustomer.setEmail(customerDto.getEmail());

        // si no esta vacía la encripta
        if (customerDto.getPassword() != null && !customerDto.getPassword().isBlank()) {
            existingCustomer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
        }

        existingCustomer.setFullName(customerDto.getFullName());
        existingCustomer.setBillingAddress(customerDto.getBillingAddress());
        existingCustomer.setDefaultShippingAddress(customerDto.getDefaultShippingAddress());
        existingCustomer.setCountry(customerDto.getCountry());
        existingCustomer.setPhone(customerDto.getPhone());

        return toDto(customerRepository.save(existingCustomer));
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
    }
}
