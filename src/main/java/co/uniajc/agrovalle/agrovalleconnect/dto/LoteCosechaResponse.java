package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import java.time.LocalDate;

public record LoteCosechaResponse(
        Long id,
        String tipoProducto,
        Double cantidadKg,
        Double precioUnitario,
        LocalDate fechaCosecha,
        Boolean activo,
        Long agricultorId) {

    public static LoteCosechaResponse desde(LoteCosecha lote) {
        return new LoteCosechaResponse(
                lote.getId(),
                lote.getTipoProducto(),
                lote.getCantidadKg(),
                lote.getPrecioUnitario(),
                lote.getFechaCosecha(),
                lote.getActivo(),
                lote.getAgricultor().getId());
    }
}
