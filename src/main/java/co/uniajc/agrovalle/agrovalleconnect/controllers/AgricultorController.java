package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.AgricultorRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.AgricultorResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.services.AgricultorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}