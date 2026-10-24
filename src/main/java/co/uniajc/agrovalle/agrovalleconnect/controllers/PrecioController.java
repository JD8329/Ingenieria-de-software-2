package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.PrecioPromedioResponse;
import co.uniajc.agrovalle.agrovalleconnect.dto.TransaccionPrecioRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.TransaccionPrecioResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.TransaccionPrecio;
import co.uniajc.agrovalle.agrovalleconnect.services.PrecioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/precios")
public class PrecioController {

    private final PrecioService precioService;

    public PrecioController(PrecioService precioService) {
        this.precioService = precioService;
    }

    @PostMapping("/transacciones")
    public ResponseEntity<TransaccionPrecioResponse> registrarTransaccion(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TransaccionPrecioRequest solicitud) {
        TransaccionPrecio transaccion = precioService.registrarTransaccion(
                Long.valueOf(jwt.getSubject()), solicitud);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TransaccionPrecioResponse.desde(transaccion));
    }

    @GetMapping("/promedio")
    public ResponseEntity<PrecioPromedioResponse> consultarPromedio(
            @RequestParam String producto) {
        return ResponseEntity.ok(precioService.consultarPromedio(producto));
    }
}
