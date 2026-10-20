package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.AgricultorRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.AgricultorResponse;
import co.uniajc.agrovalle.agrovalleconnect.dto.PerfilAgricultorResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.services.AgricultorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agricultores")
public class AgricultorController {

    @Autowired
    private AgricultorService agricultorService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody AgricultorRegistroRequest request) {
        try {
            Agricultor nuevoAgricultor = agricultorService.registrarAgricultor(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AgricultorResponse.desde(nuevoAgricultor));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/{agricultorId}/perfil")
    public ResponseEntity<PerfilAgricultorResponse> perfilPublico(
            @PathVariable Long agricultorId) {
        return ResponseEntity.ok(agricultorService.obtenerPerfilPublico(agricultorId));
    }
}