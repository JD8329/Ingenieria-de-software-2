package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long comercianteId,
        String comerciante,
        Long agricultorId,
        String agricultor,
        EstadoPedido estado,
        Double total,
        Instant fechaHora,
        String transportista,
        String ruta,
        LocalDate fechaEstimadaRecepcion,
        Integer calificacion,
        String comentariosCalificacion,
        Double calificacionPromedioAgricultor,
        List<PedidoItemResponse> items) {

    public static PedidoResponse desde(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getComerciante().getId(),
                pedido.getComerciante().getNombreNegocio(),
                pedido.getAgricultor().getId(),
                pedido.getAgricultor().getNombreCompleto(),
                pedido.getEstado(),
                pedido.getTotal(),
                pedido.getFechaHora(),
                pedido.getTransportista(),
                pedido.getRuta(),
                pedido.getFechaEstimadaRecepcion(),
                pedido.getCalificacion(),
                pedido.getComentariosCalificacion(),
                pedido.getAgricultor().getCalificacionPromedio(),
                pedido.getItems().stream().map(PedidoItemResponse::desde).toList());
    }
}
