package co.uniajc.agrovalle.agrovalleconnect.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PedidoItemRequest(
        @NotNull(message = "El ID del lote es obligatorio")
        @Positive(message = "El ID del lote debe ser mayor a cero")
        Long loteId,
        @NotNull(message = "La cantidad solicitada es obligatoria")
        @Positive(message = "La cantidad solicitada debe ser mayor a cero")
        @Digits(integer = 10, fraction = 2, message = "La cantidad admite hasta 10 enteros y 2 decimales")
        BigDecimal cantidadKg) {
}
