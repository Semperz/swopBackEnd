package edu.badpals.swopbackend.repository;

import edu.badpals.swopbackend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
