package co.uniajc.agrovalle.agrovalleconnect.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransaccionRegistroRequest(
        @NotNull(message = "La cantidad vendida es obligatoria")
        @Positive(message = "La cantidad vendida debe ser mayor a cero")
        Double cantidadKg,
        @NotNull(message = "El precio unitario de venta es obligatorio")
        @Positive(message = "El precio unitario de venta debe ser mayor a cero")
        Double precioUnitario) {
}
