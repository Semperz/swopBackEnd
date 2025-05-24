package edu.badpals.swopbackend.service;

import edu.badpals.swopbackend.dto.CustomerDto;
import edu.badpals.swopbackend.model.Customer;
import edu.badpals.swopbackend.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;
    private final AuthService authService;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, AuthService authService) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.authService = authService;
    }

    private CustomerDto toDto(Customer customer) {
        return modelMapper.map(customer, CustomerDto.class);
    }

    private Customer toEntity(CustomerDto dto) {
        return modelMapper.map(dto, Customer.class);
    }

    @Transactional
    public CustomerDto createCustomer(CustomerDto customerDto) {
        customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
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

    public CustomerDto getCurrentCustomer() {
        String email = authService.getCurrentUserEmail();
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
        return toDto(customer);
    }

    @Transactional
    public CustomerDto updateCustomer(Long id, CustomerDto customerDto) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));

        modelMapper.map(customerDto, existingCustomer); // map fields automáticamente
        if (customerDto.getPassword() != null && !customerDto.getPassword().isBlank()) {
            existingCustomer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
        }

        return toDto(customerRepository.save(existingCustomer));
    }

    public CustomerDto updateCurrentCustomer(CustomerDto customerDto) {
        String email = authService.getCurrentUserEmail();
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        modelMapper.map(customerDto, customer);
        if (customerDto.getPassword() != null && !customerDto.getPassword().isBlank()) {
            customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
        }

        return toDto(customerRepository.save(customer));
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
    }

    public void deleteCurrentCustomer() {
        String email = authService.getCurrentUserEmail();
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customerRepository.delete(customer);
    }
}
