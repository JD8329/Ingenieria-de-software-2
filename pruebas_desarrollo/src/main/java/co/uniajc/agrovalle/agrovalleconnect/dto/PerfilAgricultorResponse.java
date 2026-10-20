package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import java.util.List;

public record PerfilAgricultorResponse(
        Long id,
        String nombreCompleto,
        String ubicacionFinca,
        Municipio municipio,
        Double calificacionPromedio,
        List<LoteCosechaResponse> lotesActivos) {
}
