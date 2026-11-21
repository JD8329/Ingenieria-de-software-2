package co.uniajc.agrovalle.agrovalleconnect.dto;

import java.math.BigDecimal;

public record PrecioPromedioResponse(
        String producto,
        long cantidadTransacciones,
        BigDecimal precioPromedioCop,
        int periodoHoras) {
}
