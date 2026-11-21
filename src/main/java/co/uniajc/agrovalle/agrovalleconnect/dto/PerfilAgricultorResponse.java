package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import java.math.BigDecimal;
import java.util.List;

public record PerfilAgricultorResponse(
        Long agricultorId,
        String nombreCompleto,
        String nombreFinca,
        Municipio municipio,
        BigDecimal calificacionPromedio,
        Long totalCalificaciones,
        Long lotesActivos,
        List<LoteCosechaResponse> catalogoActivo) {

    public PerfilAgricultorResponse(
            Long agricultorId,
            String nombreCompleto,
            String nombreFinca,
            BigDecimal calificacionPromedio,
            Long totalCalificaciones,
            Long lotesActivos) {
        this(agricultorId, nombreCompleto, nombreFinca, null, calificacionPromedio,
                    totalCalificaciones, lotesActivos, List.of());
    }
}
