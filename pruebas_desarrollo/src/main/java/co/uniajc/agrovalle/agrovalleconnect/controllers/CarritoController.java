package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.CarritoItemRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.CarritoItemResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.CarritoItem;
import co.uniajc.agrovalle.agrovalleconnect.services.CarritoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public ResponseEntity<List<CarritoItemResponse>> listar(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(carritoService.listar(Long.valueOf(jwt.getSubject())).stream()
                .map(CarritoItemResponse::desde)
                .toList());
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoItemResponse> agregar(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CarritoItemRequest request) {
        CarritoItem item = carritoService.agregar(Long.valueOf(jwt.getSubject()), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CarritoItemResponse.desde(item));
    }

    @DeleteMapping("/items/{loteId}")
    public ResponseEntity<Void> retirar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long loteId) {
        carritoService.retirar(Long.valueOf(jwt.getSubject()), loteId);
        return ResponseEntity.noContent().build();
    }
}
