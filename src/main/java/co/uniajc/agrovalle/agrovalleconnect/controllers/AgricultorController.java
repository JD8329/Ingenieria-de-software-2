package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.AgricultorRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.AgricultorResponse;
import co.uniajc.agrovalle.agrovalleconnect.dto.PerfilAgricultorResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.services.AgricultorService;
import co.uniajc.agrovalle.agrovalleconnect.services.PedidoService;
import jakarta.validation.Valid;
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

    private final AgricultorService agricultorService;
    private final PedidoService pedidoService;

    public AgricultorController(AgricultorService agricultorService, PedidoService pedidoService) {
        this.agricultorService = agricultorService;
        this.pedidoService = pedidoService;
    }

    @PostMapping("/registro")
    public ResponseEntity<AgricultorResponse> registrar(@Valid @RequestBody AgricultorRegistroRequest datos) {
        Agricultor nuevo = agricultorService.registrar(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(AgricultorResponse.desde(nuevo));
    }

    @GetMapping("/{id}/perfil")
    public ResponseEntity<PerfilAgricultorResponse> perfil(@PathVariable Long id) {
        PerfilAgricultorResponse perfil = pedidoService.obtenerPerfilAgricultor(id);
        return ResponseEntity.ok(perfil);
    }
}
