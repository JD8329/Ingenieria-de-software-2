package co.uniajc.agrovalle.agrovalleconnect.events;

import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;

public record PedidoEstadoCambiadoEvent(
        Long pedidoId,
        Long comercianteId,
        Long agricultorId,
        EstadoPedido estado) {
}
