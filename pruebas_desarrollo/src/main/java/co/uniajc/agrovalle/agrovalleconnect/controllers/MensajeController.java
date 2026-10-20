package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.MensajeRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.MensajeResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.Mensaje;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.services.MensajeService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api/v1/mensajes")
public class MensajeController {

    private final MensajeService mensajeService;

    public MensajeController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @PostMapping
    public ResponseEntity<MensajeResponse> enviar(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MensajeRequest request) {
        Mensaje mensaje = mensajeService.enviar(Long.valueOf(jwt.getSubject()), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(MensajeResponse.desde(mensaje));
    }

    @GetMapping
    public ResponseEntity<List<MensajeResponse>> listar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam Long loteId) {
        Rol rol = Rol.valueOf(jwt.getClaimAsString("rol"));
        List<MensajeResponse> mensajes = mensajeService
                .listar(loteId, Long.valueOf(jwt.getSubject()), rol).stream()
                .map(MensajeResponse::desde)
                .toList();
        return ResponseEntity.ok(mensajes);
    }
}
