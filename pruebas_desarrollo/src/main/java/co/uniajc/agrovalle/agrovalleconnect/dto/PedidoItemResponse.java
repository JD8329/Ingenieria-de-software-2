package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.PedidoItem;

public record PedidoItemResponse(
        Long loteId,
        String producto,
        Double cantidadKg,
        Double precioUnitario,
        Double subtotal) {

    public static PedidoItemResponse desde(PedidoItem item) {
        return new PedidoItemResponse(
                item.getLote().getId(),
                item.getLote().getTipoProducto(),
                item.getCantidadKg(),
                item.getPrecioUnitario(),
                item.getCantidadKg() * item.getPrecioUnitario());
    }
}
