package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.PedidoItemRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.PedidoRequest;
import co.uniajc.agrovalle.agrovalleconnect.exceptions.ConflictoException;
import co.uniajc.agrovalle.agrovalleconnect.exceptions.RecursoNoEncontradoException;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.DetallePedido;
import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Notificacion;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.NotificacionRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.PedidoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final LoteCosechaRepository loteCosechaRepository;
    private final NotificacionRepository notificacionRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         LoteCosechaRepository loteCosechaRepository,
                         NotificacionRepository notificacionRepository) {
        this.pedidoRepository = pedidoRepository;
        this.loteCosechaRepository = loteCosechaRepository;
        this.notificacionRepository = notificacionRepository;
    }

    @Transactional
    public Pedido reservar(PedidoRequest datos) {
        Set<Long> lotesSolicitados = new HashSet<>();
        for (PedidoItemRequest item : datos.items()) {
            if (!lotesSolicitados.add(item.loteId())) {
                throw new IllegalArgumentException("El lote " + item.loteId() + " no puede repetirse en el pedido");
            }
        }

        List<PedidoItemRequest> itemsOrdenados = datos.items().stream()
                .sorted(Comparator.comparing(PedidoItemRequest::loteId))
                .toList();
        Pedido pedido = new Pedido();
        pedido.setComercianteId(datos.comercianteId());
        pedido.setEstado(EstadoPedido.RESERVADO);
        pedido.setDetalles(new ArrayList<>());
        BigDecimal total = BigDecimal.ZERO;
        TreeMap<Long, Agricultor> agricultoresNotificados = new TreeMap<>();

        for (PedidoItemRequest item : itemsOrdenados) {
            LoteCosecha lote = loteCosechaRepository.findByIdForUpdate(item.loteId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Lote no encontrado con id " + item.loteId()));
            if (!lote.getActivo()) {
                throw new ConflictoException("El lote " + lote.getId() + " no está disponible");
            }
            if (lote.getCantidadKg().compareTo(item.cantidadKg()) < 0) {
                throw new ConflictoException("Existencias insuficientes para el lote " + lote.getId());
            }

            BigDecimal subtotal = lote.getPrecioUnitario()
                    .multiply(item.cantidadKg())
                    .setScale(2, RoundingMode.HALF_UP);
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setLote(lote);
            detalle.setCantidadKg(item.cantidadKg());
            detalle.setPrecioUnitario(lote.getPrecioUnitario());
            detalle.setSubtotal(subtotal);
            pedido.getDetalles().add(detalle);
            total = total.add(subtotal);
            lote.setCantidadKg(lote.getCantidadKg().subtract(item.cantidadKg()));
            agricultoresNotificados.put(lote.getAgricultor().getId(), lote.getAgricultor());
        }

        pedido.setTotal(total.setScale(2, RoundingMode.HALF_UP));
        Pedido guardado = pedidoRepository.save(pedido);
        List<Notificacion> notificaciones = agricultoresNotificados.values().stream()
                .map(agricultor -> notificacionPedido(agricultor, guardado.getId()))
                .toList();
        notificacionRepository.saveAll(notificaciones);
        return guardado;
    }

    private Notificacion notificacionPedido(Agricultor agricultor, Long pedidoId) {
        Notificacion notificacion = new Notificacion();
        notificacion.setAgricultor(agricultor);
        notificacion.setTipo("PEDIDO");
        notificacion.setMensaje("Nuevo pedido " + pedidoId + " reservado");
        notificacion.setReferenciaId(pedidoId);
        return notificacion;
    }
}
