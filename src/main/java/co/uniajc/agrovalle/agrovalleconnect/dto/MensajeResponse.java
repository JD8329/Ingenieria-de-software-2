package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Mensaje;
import java.time.LocalDateTime;

public record MensajeResponse(
        Long id,
        Long loteId,
        Long comercianteId,
        Long agricultorId,
        String contenido,
        LocalDateTime fechaCreacion) {

    public static MensajeResponse desde(Mensaje mensaje) {
        return new MensajeResponse(
                mensaje.getId(),
                mensaje.getLote().getId(),
                mensaje.getComercianteId(),
                mensaje.getLote().getAgricultor().getId(),
                mensaje.getContenido(),
                mensaje.getFechaCreacion());
    }
}
