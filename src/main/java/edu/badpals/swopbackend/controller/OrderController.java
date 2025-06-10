package edu.badpals.swopbackend.controller;

import edu.badpals.swopbackend.dto.OrderDto;
import edu.badpals.swopbackend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Pedidos", description = "API para gestionar los pedidos")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los pedidos", description = "Lista de pedidos registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDto>> getMyOrders() {
        List<OrderDto> orders = orderService.getOrdersForCurrentUser();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pedido por ID", description = "Recibe un pedido basado en su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        try {
            OrderDto order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo pedido", description = "Registra un nuevo pedido en la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createOrder(@RequestBody OrderDto orderDto, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario no autenticado");
            }
            String email = authentication.getName();
            OrderDto createdOrder = orderService.createOrder(orderDto, email);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear el pedido: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un pedido", description = "Modifica los datos de un pedido existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<OrderDto> updateOrder(@PathVariable Long id, @RequestBody OrderDto orderDto) {
        try {
            OrderDto updatedOrder = orderService.updateOrder(id, orderDto);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();

        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un pedido", description = "Elimina un pedido de la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id, Authentication authentication) {
        String email = authentication.getName();
        orderService.deleteOrder(id, email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/details/productsname/{orderDetailId}")
    @Operation(summary = "Obtener nombre de producto en un detalle de pedido", description = "Recibe un detalle de pedido basado en su ID y devuelve el nombre del producto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nombre del producto obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Detalle de pedido no encontrado")
    })
    public ResponseEntity<String> getProductNameByOrderDetailId(@PathVariable Long orderDetailId) {
        try {
            String productName = orderService.getProductNameByOrderDetailId(orderDetailId);
            return ResponseEntity.ok(productName);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
