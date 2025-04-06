package edu.badpals.swopbackend.repository;

import edu.badpals.swopbackend.model.Order;
import edu.badpals.swopbackend.model.OrderDetail;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Transactional
    void deleteByOrder(Order order);
}
