package co.uniajc.agrovalle.agrovalleconnect.events;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Notificacion;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import co.uniajc.agrovalle.agrovalleconnect.repositories.NotificacionRepository;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

@Component
public class NotificacionCambioEstadoObserver {

    private final NotificacionRepository notificacionRepository;

    public NotificacionCambioEstadoObserver(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @EventListener
    public void alCambiarEstado(CambioEstadoPedidoEvent evento) {
        Pedido pedido = evento.pedido();
        LinkedHashMap<Long, Agricultor> agricultores = new LinkedHashMap<>();
        pedido.getDetalles().forEach(detalle ->
                agricultores.putIfAbsent(
                        detalle.getLote().getAgricultor().getId(),
                        detalle.getLote().getAgricultor()));

        List<Notificacion> notificaciones = agricultores.values().stream()
                .map(agricultor -> crearNotificacion(
                        agricultor,
                        pedido,
                        evento.estadoAnterior(),
                        evento.estadoNuevo()))
                .toList();
        notificacionRepository.saveAll(notificaciones);
    }

    private Notificacion crearNotificacion(Agricultor agricultor,
                                           Pedido pedido,
                                           co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido anterior,
                                           co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido nuevo) {
        Notificacion notificacion = new Notificacion();
        notificacion.setAgricultor(agricultor);
        notificacion.setTipo("CAMBIO_ESTADO");
        notificacion.setMensaje("Pedido " + pedido.getId() + " cambió de "
                + anterior.name() + " a " + nuevo.name());
        notificacion.setReferenciaId(pedido.getId());
        return notificacion;
    }
}
