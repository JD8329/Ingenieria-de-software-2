package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.AgricultorRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.AgricultorResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.services.AgricultorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agricultores")
public class AgricultorController {

    private final AgricultorService agricultorService;

    public AgricultorController(AgricultorService agricultorService) {
        this.agricultorService = agricultorService;
    }

    @PostMapping("/registro")
 feature/sprint3-hu05-hu06
    public ResponseEntity<AgricultorResponse> registrar(@Valid @RequestBody AgricultorRegistroRequest datos) {
        Agricultor nuevo = agricultorService.registrar(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(AgricultorResponse.desde(nuevo));

    public ResponseEntity<?> registrar(@Valid @RequestBody AgricultorRegistroRequest request) {
        try {
            Agricultor nuevoAgricultor = agricultorService.registrarAgricultor(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AgricultorResponse.desde(nuevoAgricultor));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        main
    }
}
