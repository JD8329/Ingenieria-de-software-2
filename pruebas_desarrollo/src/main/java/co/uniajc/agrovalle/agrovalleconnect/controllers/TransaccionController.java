package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.PrecioPromedioResponse;
import co.uniajc.agrovalle.agrovalleconnect.dto.TransaccionRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.TransaccionResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.Transaccion;
import co.uniajc.agrovalle.agrovalleconnect.services.TransaccionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    @PostMapping("/api/v1/productos/{loteId}/transacciones")
    public ResponseEntity<TransaccionResponse> registrar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long loteId,
            @Valid @RequestBody TransaccionRegistroRequest request) {
        Transaccion transaccion = transaccionService.registrar(Long.valueOf(jwt.getSubject()), loteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(TransaccionResponse.desde(transaccion));
    }

    @GetMapping("/api/v1/precios/promedio")
    public ResponseEntity<PrecioPromedioResponse> precioPromedio(@RequestParam String producto) {
        return ResponseEntity.ok(transaccionService.calcularPrecioPromedio(producto));
    }
}
