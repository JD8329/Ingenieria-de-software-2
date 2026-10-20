package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.CarritoItem;

public record CarritoItemResponse(
        Long loteId,
        String producto,
        String nombreAgricultor,
        Double cantidadKg,
        Double precioUnitario,
        Double subtotal) {

    public static CarritoItemResponse desde(CarritoItem item) {
        return new CarritoItemResponse(
                item.getLote().getId(),
                item.getLote().getTipoProducto(),
                item.getLote().getAgricultor().getNombreCompleto(),
                item.getCantidadKg(),
                item.getLote().getPrecioUnitario(),
                item.getCantidadKg() * item.getLote().getPrecioUnitario());
    }
}
