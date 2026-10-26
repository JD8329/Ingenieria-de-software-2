package co.uniajc.agrovalle.agrovalleconnect.events;

public record LoteCosechaPublicadoEvent(
        Long loteId,
        Long agricultorId,
        String tipoProducto) {
}
