package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Notificacion;
import co.uniajc.agrovalle.agrovalleconnect.models.TipoNotificacion;
import java.time.Instant;

public record NotificacionResponse(
        Long id,
        TipoNotificacion tipo,
        String contenido,
        boolean leida,
        Instant fechaHora) {

    public static NotificacionResponse desde(Notificacion notificacion) {
        return new NotificacionResponse(
                notificacion.getId(),
                notificacion.getTipo(),
                notificacion.getContenido(),
                notificacion.isLeida(),
                notificacion.getFechaHora());
    }
}
