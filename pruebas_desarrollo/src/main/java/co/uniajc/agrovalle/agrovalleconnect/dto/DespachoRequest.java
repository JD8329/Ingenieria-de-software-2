package co.uniajc.agrovalle.agrovalleconnect.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record DespachoRequest(
        @NotBlank(message = "El transportista es obligatorio")
        @Size(max = 120, message = "El transportista no puede superar 120 caracteres")
        String transportista,
        @NotBlank(message = "La ruta es obligatoria")
        @Size(max = 500, message = "La ruta no puede superar 500 caracteres")
        String ruta,
        @NotNull(message = "La fecha estimada de recepción es obligatoria")
        @FutureOrPresent(message = "La fecha estimada de recepción no puede estar en el pasado")
        LocalDate fechaEstimadaRecepcion) {
}
