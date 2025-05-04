package edu.badpals.swopbackend.controller;

import edu.badpals.swopbackend.dto.BidDto;
import edu.badpals.swopbackend.model.BidStatus;
import edu.badpals.swopbackend.service.BidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
@Tag(name = "Bids", description = "Endpoints para gestionar pujas de productos")
public class BidController {

    private final BidService bidService;

    @Autowired
    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @Operation(summary = "Realiza una puja", description = "Crea una nueva puja por un producto")
    @ApiResponse(responseCode = "200", description = "Puja creada exitosamente")
    @PostMapping
    public ResponseEntity<BidDto> placeBid(@RequestBody BidDto dto) {
        return ResponseEntity.ok(bidService.placeBid(dto));
    }

    @Operation(summary = "Obtiene pujas por producto", description = "Devuelve todas las pujas realizadas para un producto")
    @ApiResponse(responseCode = "200", description = "Listado de pujas por producto")
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<BidDto>> getBidsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(bidService.getBidsByProduct(productId));
    }

    @Operation(summary = "Obtiene pujas por cliente", description = "Devuelve todas las pujas realizadas por un cliente")
    @ApiResponse(responseCode = "200", description = "Listado de pujas por cliente")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BidDto>> getBidsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(bidService.getBidsByCustomer(customerId));
    }

    @Operation(summary = "Obtiene la puja más alta de un producto", description = "Devuelve la puja con el monto más alto de un producto")
    @ApiResponse(responseCode = "200", description = "Puja más alta encontrada")
    @GetMapping("/product/{productId}/highest")
    public ResponseEntity<BidDto> getHighestBid(@PathVariable Long productId) {
        return ResponseEntity.ok(bidService.getHighestBidForProduct(productId));
    }

    @Operation(summary = "Elimina una puja", description = "Elimina una puja específica por ID")
    @ApiResponse(responseCode = "204", description = "Puja eliminada correctamente")
    @DeleteMapping("/{bidId}")
    public ResponseEntity<Void> deleteBid(@PathVariable Long bidId) {
        bidService.deleteBid(bidId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Actualiza el estado de una puja", description = "Cambia el estado de una puja específica")
    @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente")
    @PatchMapping("/{bidId}/status")
    public ResponseEntity<BidDto> updateStatus(
            @PathVariable Long bidId,
            @Parameter(description = "Nuevo estado de la puja") @RequestParam BidStatus status) {
        return ResponseEntity.ok(bidService.updateBidStatus(bidId, status));
    }

    @Operation(summary = "Obtiene todas las pujas", description = "Devuelve todas las pujas realizadas")
    @ApiResponse(responseCode = "200", description = "Listado de todas las pujas")
    @GetMapping("/all")
    public ResponseEntity<List<BidDto>> getAllBids() {
        return ResponseEntity.ok(bidService.getAllBids());
    }
}

