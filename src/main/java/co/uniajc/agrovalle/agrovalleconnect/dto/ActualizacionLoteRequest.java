package co.uniajc.agrovalle.agrovalleconnect.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ActualizacionLoteRequest(
        @Positive(message = "La cantidad debe ser mayor a cero")
        BigDecimal cantidadKg,
        @Positive(message = "El precio unitario debe ser mayor a cero")
        BigDecimal precioUnitario) {

    @AssertTrue(message = "Debe indicar cantidadKg o precioUnitario para actualizar el lote")
    public boolean isActualizacionValida() {
        return cantidadKg != null || precioUnitario != null;
    }
}
