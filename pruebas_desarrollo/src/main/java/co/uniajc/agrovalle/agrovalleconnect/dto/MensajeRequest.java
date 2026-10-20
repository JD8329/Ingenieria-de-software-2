package co.uniajc.agrovalle.agrovalleconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MensajeRequest(
        @NotNull(message = "El lote es obligatorio")
        Long loteId,
        @NotBlank(message = "El mensaje es obligatorio")
        @Size(max = 2000, message = "El mensaje no puede superar 2000 caracteres")
        String contenido) {
}
