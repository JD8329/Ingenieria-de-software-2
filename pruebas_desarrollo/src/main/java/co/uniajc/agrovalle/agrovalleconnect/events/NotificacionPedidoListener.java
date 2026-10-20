package co.uniajc.agrovalle.agrovalleconnect.events;

import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.models.TipoNotificacion;
import co.uniajc.agrovalle.agrovalleconnect.services.NotificacionService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificacionPedidoListener {

    private final NotificacionService notificacionService;

    public NotificacionPedidoListener(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @EventListener
    public void notificarParticipantes(PedidoEstadoCambiadoEvent event) {
        String contenido = "El pedido #" + event.pedidoId()
                + " cambió al estado " + event.estado() + ".";
        notificacionService.crear(
                event.comercianteId(), Rol.COMERCIANTE,
                TipoNotificacion.PEDIDO_ESTADO_CAMBIADO, contenido);
        notificacionService.crear(
                event.agricultorId(), Rol.PRODUCCION,
                TipoNotificacion.PEDIDO_ESTADO_CAMBIADO, contenido);
    }
}
