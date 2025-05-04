package edu.badpals.swopbackend.repository;


import edu.badpals.swopbackend.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findByProductId(Long productId);
    List<Bid> findByCustomerId(Long customerId);
    Optional<Bid> findTopByProductIdOrderByBidAmountDesc(Long productId); // Para encontrar la puja más alta
}
