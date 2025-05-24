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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;

    @Autowired
    public BidService(BidRepository bidRepository,
                      ProductRepository productRepository,
                      CustomerRepository customerRepository,
                      ModelMapper modelMapper, AuthService authService) {
        this.bidRepository = bidRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.authService = authService;
    }

    private BidDto toDto(Bid bid) {
        BidDto dto = modelMapper.map(bid, BidDto.class);
        dto.setProductId(bid.getProduct().getId());
        dto.setCustomerId(bid.getCustomer().getId());
        return dto;
    }

    private Bid toEntity(BidDto dto, Customer customer) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

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

    @Transactional
    public BidDto placeBid(BidDto dto) {
        String email = authService.getCurrentUserEmail();
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));

        Bid bid = toEntity(dto, customer);
        Bid saved = bidRepository.save(bid);
        return toDto(saved);
    }

    public List<BidDto> getBidsByProduct(Long productId) {
        return bidRepository.findByProductId(productId)
                .stream().map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<BidDto> getBidsForCurrentUser() {
        String email = authService.getCurrentUserEmail();
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));

        return bidRepository.findByCustomerId(customer.getId())
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

    @Transactional
    public void deleteBid(Long bidId) {
        String email = authService.getCurrentUserEmail();
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        if (!bid.getCustomer().getEmail().equals(email)) {
            throw new RuntimeException("You are not authorized to delete this bid.");
        }

        bidRepository.deleteById(bidId);
    }

    @Transactional
    public BidDto updateBid(Long bidId, BidDto bidDto) {
        String email = authService.getCurrentUserEmail();
        Bid existingBid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        if (!existingBid.getCustomer().getEmail().equals(email)) {
            throw new RuntimeException("You are not authorized to update this bid.");
        }

        existingBid.setBidAmount(bidDto.getBidAmount());
        existingBid.setBidTime(LocalDateTime.now());

        return toDto(bidRepository.save(existingBid));
    }

    @Transactional
    public BidDto updateBidStatus(Long bidId, BidStatus status) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));
        bid.setStatus(status);
        return toDto(bidRepository.save(bid));
    }
}

