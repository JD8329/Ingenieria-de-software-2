package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.CategoriaProducto;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LoteCosechaRequest(
        @NotBlank(message = "El tipo de producto es obligatorio")
        String tipoProducto,
        @NotNull(message = "La categoria es obligatoria")
        CategoriaProducto categoria,
        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        BigDecimal cantidadKg,
        @NotNull(message = "El precio unitario es obligatorio")
        @Positive(message = "El precio debe ser mayor a cero")
        BigDecimal precioUnitario,
        @NotNull(message = "La fecha de cosecha es obligatoria")
        @FutureOrPresent(message = "La fecha de cosecha no puede ser anterior al dia actual")
        LocalDate fechaCosecha) {
}
