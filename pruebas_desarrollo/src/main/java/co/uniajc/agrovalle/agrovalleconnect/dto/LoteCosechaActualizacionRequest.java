package co.uniajc.agrovalle.agrovalleconnect.dto;

import jakarta.validation.constraints.Positive;

public record LoteCosechaActualizacionRequest(
        @Positive(message = "La cantidad debe ser mayor a cero")
        Double cantidadKg,
        @Positive(message = "El precio debe ser mayor a cero")
        Double precioUnitario) {
}
