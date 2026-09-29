package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.LoteCosechaResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.services.LoteCosechaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/productos")
public class LoteCosechaController {

    private final LoteCosechaService loteCosechaService;

    public LoteCosechaController(LoteCosechaService loteCosechaService) {
        this.loteCosechaService = loteCosechaService;
    }

    @PostMapping
    public ResponseEntity<LoteCosechaResponse> publicarLote(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody LoteCosecha lote) {
            LoteCosecha nuevoLote = loteCosechaService.publicarLote(
                    Long.valueOf(jwt.getSubject()), lote);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(LoteCosechaResponse.desde(nuevoLote));
    }

    @GetMapping
    public ResponseEntity<List<LoteCosechaResponse>> listarLotes() {
        return ResponseEntity.ok(loteCosechaService.listarLotesActivos().stream()
                .map(LoteCosechaResponse::desde)
                .toList());
    }

    @GetMapping("/agricultor/{agricultorId}")
    public ResponseEntity<List<LoteCosechaResponse>> listarPorAgricultor(
            @PathVariable Long agricultorId) {
        return ResponseEntity.ok(loteCosechaService.listarLotesPorAgricultor(agricultorId).stream()
                .map(LoteCosechaResponse::desde)
                .toList());
    }
}