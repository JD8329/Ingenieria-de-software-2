package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import java.math.BigDecimal;

public record ReporteVentasMunicipioResponse(
        Municipio municipio,
        long cantidadPedidos,
        BigDecimal cantidadKg,
        BigDecimal totalVentas) {
}
