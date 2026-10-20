package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;

public record VentasMunicipioResponse(
        Municipio municipio,
        Long pedidosFinalizados,
        Double volumenVendidoKg,
        Double totalVentas) {
}
