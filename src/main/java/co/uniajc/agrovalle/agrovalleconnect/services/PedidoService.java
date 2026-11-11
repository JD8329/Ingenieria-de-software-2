package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.CalificacionRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.DespachoRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.PerfilAgricultorResponse;
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
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.NotificacionRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.PedidoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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
    private final AgricultorRepository agricultorRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         LoteCosechaRepository loteCosechaRepository,
                         NotificacionRepository notificacionRepository,
                         AgricultorRepository agricultorRepository) {
        this.pedidoRepository = pedidoRepository;
        this.loteCosechaRepository = loteCosechaRepository;
        this.notificacionRepository = notificacionRepository;
        this.agricultorRepository = agricultorRepository;
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

    @Transactional
    public Pedido programarDespacho(Long pedidoId, DespachoRequest datos, Long agricultorId) {
        Pedido pedido = obtenerPedido(pedidoId);
        if (pedido.getEstado() != EstadoPedido.RESERVADO) {
            throw new IllegalArgumentException("Solo se puede programar despacho para pedidos en estado RESERVADO");
        }
        if (!pedido.getDetalles().isEmpty() && agricultorId != null
                && pedido.getDetalles().stream()
                        .map(detalle -> detalle.getLote().getAgricultor().getId())
                        .noneMatch(agricultorId::equals)) {
            throw new IllegalArgumentException("El pedido no pertenece al agricultor autenticado");
        }
        if (datos.fechaEstimadaEntrega() == null || !datos.fechaEstimadaEntrega().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha estimada de entrega debe ser futura");
        }
        if (datos.ruta() == null || datos.ruta().isEmpty()) {
            throw new IllegalArgumentException("La ruta de despacho es obligatoria");
        }

        pedido.setEstado(EstadoPedido.EN_TRANSITO);
        pedido.setTransportadora(datos.transportadora());
        pedido.setConductor(datos.conductor());
        pedido.setVehiculo(datos.vehiculo());
        pedido.setFechaEstimadaEntrega(datos.fechaEstimadaEntrega());
        pedido.setRuta(String.join(" -> ", datos.ruta()));
        pedido.setObservaciones(datos.observaciones());
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido calificarPedido(Long pedidoId, CalificacionRequest datos) {
        Pedido pedido = obtenerPedido(pedidoId);
        if (!pedido.getComercianteId().equals(datos.comercianteId())) {
            throw new IllegalArgumentException("El pedido no corresponde al comerciante que intenta calificarlo");
        }
        if (pedido.getEstado() != EstadoPedido.EN_TRANSITO && pedido.getEstado() != EstadoPedido.ENTREGADO) {
            throw new IllegalArgumentException("Solo se puede calificar un pedido en tránsito o entregado");
        }
        if (datos.calificacion() == null || datos.calificacion() < 1 || datos.calificacion() > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
        }
        pedido.setCalificacion(datos.calificacion());
        pedido.setComentarioCalificacion(datos.comentario());
        pedido.setFechaEntrega(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.FINALIZADO);
        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public Pedido obtenerPedido(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id " + pedidoId));
    }

    @Transactional(readOnly = true)
    public PerfilAgricultorResponse obtenerPerfilAgricultor(Long agricultorId) {
        Agricultor agricultor = agricultorRepository.findById(agricultorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Agricultor no encontrado con id " + agricultorId));

        List<Integer> calificaciones = pedidoRepository.findAll().stream()
                .filter(pedido -> pedido.getCalificacion() != null)
                .filter(pedido -> pedido.getDetalles().stream()
                        .map(detalle -> detalle.getLote().getAgricultor().getId())
                        .anyMatch(agricultorId::equals))
                .map(Pedido::getCalificacion)
                .toList();

        BigDecimal promedio = calificaciones.isEmpty()
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(calificaciones.stream()
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0d))
                        .setScale(2, RoundingMode.HALF_UP);

        long lotesActivos = loteCosechaRepository.findAll().stream()
                .filter(lote -> Boolean.TRUE.equals(lote.getActivo()))
                .filter(lote -> lote.getAgricultor() != null && agricultorId.equals(lote.getAgricultor().getId()))
                .count();

        return new PerfilAgricultorResponse(
                agricultor.getId(),
                agricultor.getNombreCompleto(),
                agricultor.getNombreFinca(),
                promedio,
                (long) calificaciones.size(),
                lotesActivos);
    }

    @Transactional(readOnly = true)
    public BigDecimal obtenerPromedioCalificacionAgricultor(Long agricultorId) {
        List<Integer> calificaciones = pedidoRepository.findAll().stream()
                .filter(pedido -> pedido.getCalificacion() != null)
                .filter(pedido -> pedido.getDetalles().stream()
                        .map(detalle -> detalle.getLote().getAgricultor().getId())
                        .anyMatch(agricultorId::equals))
                .map(Pedido::getCalificacion)
                .toList();
        if (calificaciones.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(calificaciones.stream().mapToInt(Integer::intValue).average().orElse(0d))
                .setScale(2, RoundingMode.HALF_UP);
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
