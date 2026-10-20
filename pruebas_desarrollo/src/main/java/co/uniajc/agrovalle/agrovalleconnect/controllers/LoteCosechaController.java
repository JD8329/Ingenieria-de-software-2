package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.LoteCosechaResponse;
import co.uniajc.agrovalle.agrovalleconnect.dto.LoteCosechaActualizacionRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.LoteCosechaRequest;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.services.LoteCosechaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            @Valid @RequestBody LoteCosechaRequest request) {
        LoteCosecha nuevoLote = loteCosechaService.publicarLote(
                Long.valueOf(jwt.getSubject()), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(LoteCosechaResponse.desde(nuevoLote));
    }

    @PatchMapping("/{loteId}")
    public ResponseEntity<LoteCosechaResponse> actualizarLote(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long loteId,
            @Valid @RequestBody LoteCosechaActualizacionRequest request) {
        LoteCosecha lote = loteCosechaService.actualizarLote(
                Long.valueOf(jwt.getSubject()), loteId, request);
        return ResponseEntity.ok(LoteCosechaResponse.desde(lote));
    }

    @DeleteMapping("/{loteId}")
    public ResponseEntity<LoteCosechaResponse> desactivarLote(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long loteId) {
        LoteCosecha lote = loteCosechaService.desactivarLote(
                Long.valueOf(jwt.getSubject()), loteId);
        return ResponseEntity.ok(LoteCosechaResponse.desde(lote));
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

    @GetMapping("/buscar")
    public ResponseEntity<List<LoteCosechaResponse>> buscarLotes(
            @RequestParam(required = false) String municipio,
            @RequestParam(required = false) String categoria) {
        return ResponseEntity.ok(loteCosechaService.buscarLotesActivos(municipio, categoria).stream()
                .map(LoteCosechaResponse::desde)
                .toList());
    }
}