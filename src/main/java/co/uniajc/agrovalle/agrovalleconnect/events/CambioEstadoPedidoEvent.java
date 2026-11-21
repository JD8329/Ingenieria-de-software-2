package co.uniajc.agrovalle.agrovalleconnect.events;

import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;

public record CambioEstadoPedidoEvent(
        Pedido pedido,
        EstadoPedido estadoAnterior,
        EstadoPedido estadoNuevo) {
}
