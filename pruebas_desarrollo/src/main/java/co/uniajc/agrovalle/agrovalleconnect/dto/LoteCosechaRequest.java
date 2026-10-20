package co.uniajc.agrovalle.agrovalleconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.FutureOrPresent;
import java.time.LocalDate;

public record LoteCosechaRequest(
        @NotBlank(message = "El tipo de producto es obligatorio")
        String tipoProducto,
        @NotBlank(message = "La categoria es obligatoria")
        String categoria,
        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        Double cantidadKg,
        @NotNull(message = "El precio unitario es obligatorio")
        @Positive(message = "El precio debe ser mayor a cero")
        Double precioUnitario,
        @NotNull(message = "La fecha de cosecha es obligatoria")
        @FutureOrPresent(message = "La fecha de cosecha no puede ser anterior al dia actual")
        LocalDate fechaCosecha) {
}
