package edu.badpals.swopbackend.dto;

import edu.badpals.swopbackend.model.BidStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidDto {

    private Long id;
    private Long productId;
    private Long customerId;
    private BigDecimal bidAmount;
    private LocalDateTime bidTime;
    private BidStatus status;

    public BidDto(Long id, Long productId, Long customerId, BigDecimal bidAmount, LocalDateTime bidTime, BidStatus status) {
        this.id = id;
        this.productId = productId;
        this.customerId = customerId;
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
        this.status = status;

    }

    public BidDto() {
    }

    public BidStatus getStatus() {
        return status;
    }

    public void setStatus(BidStatus status) {
        this.status = status;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }

    public void setBidTime(LocalDateTime bidTime) {
        this.bidTime = bidTime;
    }
}

