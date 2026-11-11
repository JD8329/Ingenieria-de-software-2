package co.uniajc.agrovalle.agrovalleconnect.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CalificacionRequest(
        @NotNull(message = "El ID temporal del comerciante es obligatorio")
        @Positive(message = "El ID del comerciante debe ser mayor a cero")
        Long comercianteId,
        @NotNull(message = "La calificación es obligatoria")
        @Min(value = 1, message = "La calificación debe estar entre 1 y 5")
        @Max(value = 5, message = "La calificación debe estar entre 1 y 5")
        Integer calificacion,
        @Size(max = 500, message = "El comentario no puede superar 500 caracteres")
        String comentario) {
}
