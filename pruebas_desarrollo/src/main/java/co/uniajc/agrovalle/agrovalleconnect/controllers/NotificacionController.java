package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.NotificacionResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.Notificacion;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.services.NotificacionService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public ResponseEntity<List<NotificacionResponse>> listar(@AuthenticationPrincipal Jwt jwt) {
        Rol rol = Rol.valueOf(jwt.getClaimAsString("rol"));
        return ResponseEntity.ok(notificacionService
                .listar(Long.valueOf(jwt.getSubject()), rol).stream()
                .map(NotificacionResponse::desde)
                .toList());
    }

    @PatchMapping("/{notificacionId}/leer")
    public ResponseEntity<NotificacionResponse> marcarLeida(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long notificacionId) {
        Rol rol = Rol.valueOf(jwt.getClaimAsString("rol"));
        Notificacion notificacion = notificacionService.marcarLeida(
                Long.valueOf(jwt.getSubject()), rol, notificacionId);
        return ResponseEntity.ok(NotificacionResponse.desde(notificacion));
    }
}
