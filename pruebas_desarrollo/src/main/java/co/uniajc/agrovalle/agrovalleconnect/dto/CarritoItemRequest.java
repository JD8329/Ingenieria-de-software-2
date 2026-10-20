package co.uniajc.agrovalle.agrovalleconnect.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CarritoItemRequest(
        @NotNull(message = "El lote es obligatorio")
        Long loteId,
        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        Double cantidadKg) {
}
