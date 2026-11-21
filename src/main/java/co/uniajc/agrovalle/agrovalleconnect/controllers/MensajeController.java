package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.MensajeRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.MensajeResponse;
import co.uniajc.agrovalle.agrovalleconnect.services.MensajeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mensajes")
public class MensajeController {

    private final MensajeService mensajeService;

    public MensajeController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @PostMapping
    public ResponseEntity<MensajeResponse> enviar(@Valid @RequestBody MensajeRequest datos) {
        MensajeResponse respuesta = MensajeResponse.desde(mensajeService.enviar(datos));
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public List<MensajeResponse> historial(
            @RequestParam Long loteId,
            @RequestParam Long comercianteId) {
        return mensajeService.historial(loteId, comercianteId).stream()
                .map(MensajeResponse::desde)
                .toList();
    }
}
