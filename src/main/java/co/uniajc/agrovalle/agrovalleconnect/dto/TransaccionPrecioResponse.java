package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.TransaccionPrecio;
import java.math.BigDecimal;
import java.time.Instant;

public record TransaccionPrecioResponse(
        Long id,
        String producto,
        String municipio,
        BigDecimal precioUnitarioCop,
        Instant registradaEn) {

    public static TransaccionPrecioResponse desde(TransaccionPrecio transaccion) {
        return new TransaccionPrecioResponse(
                transaccion.getId(),
                transaccion.getProducto(),
                transaccion.getMunicipio().name(),
                transaccion.getPrecioUnitarioCop(),
                transaccion.getRegistradaEn());
    }
}
