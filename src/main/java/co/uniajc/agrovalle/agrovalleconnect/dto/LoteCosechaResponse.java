package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.CategoriaProducto;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LoteCosechaResponse(
        Long id,
        String tipoProducto,
        CategoriaProducto categoria,
        BigDecimal cantidadKg,
        BigDecimal precioUnitario,
        LocalDate fechaCosecha,
        Boolean activo,
        Long agricultorId,
        String agricultorNombre,
        Municipio municipio,
        String nombreFinca) {

    public static LoteCosechaResponse desde(LoteCosecha l) {
        return new LoteCosechaResponse(l.getId(), l.getTipoProducto(), l.getCategoria(),
                l.getCantidadKg(), l.getPrecioUnitario(), l.getFechaCosecha(), l.getActivo(),
                l.getAgricultor().getId(), l.getAgricultor().getNombreCompleto(),
                l.getAgricultor().getMunicipio(), l.getAgricultor().getNombreFinca());
    }
}
