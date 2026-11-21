package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Notificacion;
import java.time.LocalDateTime;

public record NotificacionResponse(
        Long id,
        Long agricultorId,
        String tipo,
        String mensaje,
        Long referenciaId,
        LocalDateTime fechaCreacion) {

    public static NotificacionResponse desde(Notificacion notificacion) {
        return new NotificacionResponse(
                notificacion.getId(),
                notificacion.getAgricultor().getId(),
                notificacion.getTipo(),
                notificacion.getMensaje(),
                notificacion.getReferenciaId(),
                notificacion.getFechaCreacion());
    }
}
