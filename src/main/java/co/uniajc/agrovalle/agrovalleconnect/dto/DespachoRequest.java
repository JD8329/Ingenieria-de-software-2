package co.uniajc.agrovalle.agrovalleconnect.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public record DespachoRequest(
        @NotBlank(message = "La transportadora es obligatoria")
        String transportadora,
        @NotBlank(message = "El conductor es obligatorio")
        String conductor,
        @NotBlank(message = "El vehículo es obligatorio")
        String vehiculo,
        @NotNull(message = "La fecha estimada de entrega es obligatoria")
        @Future(message = "La fecha estimada de entrega debe ser futura")
        LocalDateTime fechaEstimadaEntrega,
        @NotNull(message = "La ruta de despacho es obligatoria")
        @Size(min = 1, max = 10, message = "La ruta debe contener entre 1 y 10 puntos")
        List<@NotBlank(message = "Cada punto de la ruta es obligatorio") String> ruta,
        @Size(max = 500, message = "Las observaciones no pueden superar 500 caracteres")
        String observaciones) {
}
