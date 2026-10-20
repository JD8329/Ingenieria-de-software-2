package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Transaccion;
import java.time.Instant;

public record TransaccionResponse(
        Long id,
        Long loteId,
        Double cantidadKg,
        Double precioUnitario,
        Instant fechaHora) {

    public static TransaccionResponse desde(Transaccion transaccion) {
        return new TransaccionResponse(
                transaccion.getId(),
                transaccion.getLote().getId(),
                transaccion.getCantidadKg(),
                transaccion.getPrecioUnitario(),
                transaccion.getFechaHora());
    }
}
