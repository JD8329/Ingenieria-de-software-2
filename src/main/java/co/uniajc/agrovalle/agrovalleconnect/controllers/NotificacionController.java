package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.NotificacionResponse;
import co.uniajc.agrovalle.agrovalleconnect.services.NotificacionService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/agricultores/{agricultorId}")
    public List<NotificacionResponse> listarPorAgricultor(@PathVariable Long agricultorId) {
        return notificacionService.listarPorAgricultor(agricultorId).stream()
                .map(NotificacionResponse::desde)
                .toList();
    }
}
