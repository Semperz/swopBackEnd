package edu.badpals.swopbackend.repository;

import edu.badpals.swopbackend.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
