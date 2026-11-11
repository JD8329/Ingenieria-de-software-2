package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.DetallePedido;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long comercianteId,
        String estado,
        BigDecimal total,
        LocalDateTime fechaCreacion,
        List<PedidoDetalleResponse> detalles) {

    public static PedidoResponse desde(Pedido pedido) {
        List<PedidoDetalleResponse> detalles = pedido.getDetalles().stream()
                .map(PedidoDetalleResponse::desde)
                .toList();
        return new PedidoResponse(
                pedido.getId(),
                pedido.getComercianteId(),
                pedido.getEstado().name(),
                pedido.getTotal(),
                pedido.getFechaCreacion(),
                detalles);
    }

    public record PedidoDetalleResponse(
            Long loteId,
            String producto,
            BigDecimal cantidadKg,
            BigDecimal precioUnitario,
            BigDecimal subtotal) {

        private static PedidoDetalleResponse desde(DetallePedido detalle) {
            return new PedidoDetalleResponse(
                    detalle.getLote().getId(),
                    detalle.getLote().getTipoProducto(),
                    detalle.getCantidadKg(),
                    detalle.getPrecioUnitario(),
                    detalle.getSubtotal());
        }
    }
}
