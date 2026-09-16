package co.uniajc.agrovalle.agrovalleconnect.controllers;

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
    public ResponseEntity<?> registrar(@Valid @RequestBody Agricultor agricultor) {
        try {
            Agricultor nuevoAgricultor = agricultorService.registrarAgricultor(agricultor);
            nuevoAgricultor.setContrasena(null); // nunca devolver la contrasena en la respuesta
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAgricultor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}