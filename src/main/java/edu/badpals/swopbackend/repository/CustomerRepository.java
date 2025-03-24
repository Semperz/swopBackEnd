package edu.badpals.swopbackend.repository;

import edu.badpals.swopbackend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
