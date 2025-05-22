package edu.badpals.swopbackend.service;

import edu.badpals.swopbackend.dto.BidDto;
import edu.badpals.swopbackend.model.Bid;
import edu.badpals.swopbackend.model.BidStatus;
import edu.badpals.swopbackend.model.Customer;
import edu.badpals.swopbackend.model.Product;
import edu.badpals.swopbackend.repository.BidRepository;
import edu.badpals.swopbackend.repository.CustomerRepository;
import edu.badpals.swopbackend.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public BidService(BidRepository bidRepository,
                      ProductRepository productRepository,
                      CustomerRepository customerRepository,
                      ModelMapper modelMapper) {
        this.bidRepository = bidRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    private BidDto toDto(Bid bid) {
        BidDto dto = modelMapper.map(bid, BidDto.class);
        dto.setProductId(bid.getProduct().getId());
        dto.setCustomerId(bid.getCustomer().getId());
        return dto;
    }

    private Bid toEntity(BidDto dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Bid bid = modelMapper.map(dto, Bid.class);
        bid.setProduct(product);
        bid.setCustomer(customer);
        if (bid.getBidTime() == null) {
            bid.setBidTime(LocalDateTime.now());
        }
        if (bid.getStatus() == null) {
            bid.setStatus(BidStatus.PENDING);
        }
        return bid;
    }

    public BidDto placeBid(BidDto dto) {
        Bid bid = toEntity(dto);
        Bid saved = bidRepository.save(bid);
        return toDto(saved);
    }

    public List<BidDto> getBidsByProduct(Long productId) {
        return bidRepository.findByProductId(productId)
                .stream().map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<BidDto> getBidsByCustomer(Long customerId) {
        return bidRepository.findByCustomerId(customerId)
                .stream().map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<BidDto> getAllBids() {
        return bidRepository.findAll()
                .stream().map(this::toDto)
                .collect(Collectors.toList());
    }

    public BidDto getHighestBidForProduct(Long productId) {
        return bidRepository.findTopByProductIdOrderByBidAmountDesc(productId)
                .map(this::toDto)
                .orElse(null);
    }

    public void deleteBid(Long bidId) {
        if (!bidRepository.existsById(bidId)) {
            throw new RuntimeException("Bid not found");
        }
        bidRepository.deleteById(bidId);
    }

    public BidDto updateBidStatus(Long bidId, BidStatus status) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));
        bid.setStatus(status);
        return toDto(bidRepository.save(bid));
    }
}

