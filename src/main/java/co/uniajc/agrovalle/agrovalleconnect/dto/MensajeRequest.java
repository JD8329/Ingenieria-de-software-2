package co.uniajc.agrovalle.agrovalleconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MensajeRequest(
        @NotNull(message = "El ID temporal del comerciante es obligatorio")
        @Positive(message = "El ID del comerciante debe ser mayor a cero")
        Long comercianteId,
        @NotNull(message = "El ID del lote es obligatorio")
        @Positive(message = "El ID del lote debe ser mayor a cero")
        Long loteId,
        @NotBlank(message = "El contenido del mensaje es obligatorio")
        @Size(max = 2000, message = "El mensaje no puede superar 2000 caracteres")
        String contenido) {
}
