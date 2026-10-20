package co.uniajc.agrovalle.agrovalleconnect.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CalificacionRequest(
        @NotNull(message = "La calificación es obligatoria")
        @Min(value = 1, message = "La calificación mínima es 1")
        @Max(value = 5, message = "La calificación máxima es 5")
        Integer calificacion,
        @NotBlank(message = "Los comentarios son obligatorios")
        @Size(max = 2000, message = "Los comentarios no pueden superar 2000 caracteres")
        String comentarios) {
}
