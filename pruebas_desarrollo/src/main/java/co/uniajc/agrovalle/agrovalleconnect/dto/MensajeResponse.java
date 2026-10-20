package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Mensaje;
import java.time.Instant;

public record MensajeResponse(
        Long id,
        Long loteId,
        Long comercianteId,
        String comerciante,
        Long agricultorId,
        String agricultor,
        String contenido,
        Instant fechaHora) {

    public static MensajeResponse desde(Mensaje mensaje) {
        return new MensajeResponse(
                mensaje.getId(),
                mensaje.getLote().getId(),
                mensaje.getComerciante().getId(),
                mensaje.getComerciante().getNombreNegocio(),
                mensaje.getAgricultor().getId(),
                mensaje.getAgricultor().getNombreCompleto(),
                mensaje.getContenido(),
                mensaje.getFechaHora());
    }
}
