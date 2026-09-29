package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.LoteCosechaRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.LoteCosechaResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.services.LoteCosechaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    // TEMPORAL: el agricultorId llega por parametro hasta que exista login con JWT (HU-11)
    @PostMapping
    public ResponseEntity<LoteCosechaResponse> publicar(
            @RequestParam Long agricultorId,
            @Valid @RequestBody LoteCosechaRequest datos) {
        LoteCosecha nuevo = loteCosechaService.publicar(agricultorId, datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(LoteCosechaResponse.desde(nuevo));
    }

    @GetMapping
    public List<LoteCosechaResponse> listar() {
        return loteCosechaService.listarActivos().stream().map(LoteCosechaResponse::desde).toList();
    }

    @GetMapping("/agricultor/{agricultorId}")
    public List<LoteCosechaResponse> listarPorAgricultor(@PathVariable Long agricultorId) {
        return loteCosechaService.listarPorAgricultor(agricultorId).stream()
                .map(LoteCosechaResponse::desde).toList();
    }
}
