package edu.badpals.swopbackend.controller;

import edu.badpals.swopbackend.dto.CustomerDto;
import edu.badpals.swopbackend.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Clientes", description = "API para gestionar clientes")  // Swagger tag
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los clientes", description = "Lista de clientes registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida exitosamente")
    })
    @PreAuthorize("hasRole('ADMIN')") // Solo administradores pueden ver todos los clientes
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        List<CustomerDto> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    // Obtener un cliente por ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un cliente por ID", description = "Recibe un cliente basado en su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PreAuthorize("isAuthenticated()") // Solo administradores o el propio cliente
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) {
        try {
            CustomerDto customer = customerService.getCustomerById(id);
            return ResponseEntity.ok(customer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo cliente", description = "Registra un nuevo cliente en la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerDto customerDto) {
        CustomerDto newCustomer = customerService.createCustomer(customerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCustomer);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un cliente", description = "Modifica los datos de un cliente existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')") // Solo administradores pueden actualizar clientes
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long id, @RequestBody CustomerDto customerDto) {
        try {
            CustomerDto updatedCustomer = customerService.updateCustomer(id, customerDto);
            return ResponseEntity.ok(updatedCustomer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un cliente", description = "Elimina un cliente de la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Usuario autenticado ve su propio perfil
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public ResponseEntity<CustomerDto> getCurrentCustomer() {
        return ResponseEntity.ok(customerService.getCurrentCustomer());
    }

    // Usuario autenticado solo actualiza su perfil
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/me")
    public ResponseEntity<CustomerDto> updateCurrentCustomer(@RequestBody CustomerDto customerDto) {
        return ResponseEntity.ok(customerService.updateCurrentCustomer(customerDto));
    }

    // Usuario autenticado elimina su propio perfil
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentCustomer() {
        customerService.deleteCurrentCustomer();
        return ResponseEntity.noContent().build();
    }
}

