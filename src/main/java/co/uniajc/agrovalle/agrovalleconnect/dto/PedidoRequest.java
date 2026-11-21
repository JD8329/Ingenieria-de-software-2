package co.uniajc.agrovalle.agrovalleconnect.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PedidoRequest(
        @NotNull(message = "El ID temporal del comerciante es obligatorio")
        @Positive(message = "El ID del comerciante debe ser mayor a cero")
        Long comercianteId,
        @NotEmpty(message = "El pedido debe contener al menos un lote")
        @Size(max = 100, message = "El pedido no puede contener más de 100 lotes")
        List<@Valid PedidoItemRequest> items) {
}
