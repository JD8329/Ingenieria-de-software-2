package co.uniajc.agrovalle.agrovalleconnect.dto;

import java.math.BigDecimal;

public record PerfilAgricultorResponse(
        Long agricultorId,
        String nombreCompleto,
        String nombreFinca,
        BigDecimal calificacionPromedio,
        Long totalCalificaciones,
        Long lotesActivos) {
}
