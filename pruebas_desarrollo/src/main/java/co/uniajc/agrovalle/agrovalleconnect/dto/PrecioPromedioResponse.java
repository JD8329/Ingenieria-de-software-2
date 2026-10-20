package co.uniajc.agrovalle.agrovalleconnect.dto;

import java.time.Instant;

public record PrecioPromedioResponse(
        String producto,
        long cantidadTransacciones,
        Double precioPromedio,
        String moneda,
        Instant desde,
        Instant hasta) {
}
