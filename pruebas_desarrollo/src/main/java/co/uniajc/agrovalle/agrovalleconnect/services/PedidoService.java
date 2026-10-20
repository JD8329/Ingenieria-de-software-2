package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.CalificacionRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.DespachoRequest;
import co.uniajc.agrovalle.agrovalleconnect.events.PedidoEstadoCambiadoEvent;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.CarritoItem;
import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;
import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import co.uniajc.agrovalle.agrovalleconnect.models.PedidoItem;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.CarritoItemRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.ComercianteRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.PedidoRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PedidoService {

    private final CarritoItemRepository carritoItemRepository;
    private final ComercianteRepository comercianteRepository;
    private final AgricultorRepository agricultorRepository;
    private final LoteCosechaRepository loteCosechaRepository;
    private final PedidoRepository pedidoRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PedidoService(
            CarritoItemRepository carritoItemRepository,
            ComercianteRepository comercianteRepository,
            AgricultorRepository agricultorRepository,
            LoteCosechaRepository loteCosechaRepository,
            PedidoRepository pedidoRepository,
            ApplicationEventPublisher eventPublisher) {
        this.carritoItemRepository = carritoItemRepository;
        this.comercianteRepository = comercianteRepository;
        this.agricultorRepository = agricultorRepository;
        this.loteCosechaRepository = loteCosechaRepository;
        this.pedidoRepository = pedidoRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public List<Pedido> checkout(Long comercianteId) {
        Comerciante comerciante = comercianteRepository.findById(comercianteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Comerciante no encontrado"));
        List<CarritoItem> carrito = carritoItemRepository
                .findByComercianteIdOrderByLoteId(comercianteId);
        if (carrito.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El carrito esta vacio");
        }

        Map<Long, Pedido> pedidosPorAgricultor = new LinkedHashMap<>();
        for (CarritoItem item : carrito) {
            LoteCosecha lote = loteCosechaRepository.buscarPorIdConBloqueo(item.getLote().getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Uno de los lotes ya no existe"));
            if (!Boolean.TRUE.equals(lote.getActivo())
                    || item.getCantidadKg() > lote.getCantidadKg()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Stock insuficiente para el lote " + lote.getId());
            }
            Agricultor agricultor = agricultorRepository.findById(lote.getAgricultor().getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Agricultor no encontrado"));
            Pedido pedido = pedidosPorAgricultor.computeIfAbsent(agricultor.getId(), ignored -> {
                Pedido nuevo = new Pedido();
                nuevo.setComerciante(comerciante);
                nuevo.setAgricultor(agricultor);
                nuevo.setEstado(EstadoPedido.RESERVADO);
                nuevo.setFechaHora(Instant.now());
                nuevo.setTotal(0.0);
                return nuevo;
            });

            PedidoItem pedidoItem = new PedidoItem();
            pedidoItem.setLote(lote);
            pedidoItem.setCantidadKg(item.getCantidadKg());
            pedidoItem.setPrecioUnitario(lote.getPrecioUnitario());
            pedido.agregarItem(pedidoItem);
            pedido.setTotal(pedido.getTotal() + item.getCantidadKg() * lote.getPrecioUnitario());

            lote.setCantidadKg(lote.getCantidadKg() - item.getCantidadKg());
            if (lote.getCantidadKg() == 0) {
                lote.setActivo(false);
            }
        }

        List<Pedido> pedidos = pedidoRepository.saveAll(new ArrayList<>(pedidosPorAgricultor.values()));
        for (Pedido pedido : pedidos) {
            publicarCambioEstado(pedido);
        }
        carritoItemRepository.deleteByComercianteId(comercianteId);
        return pedidos;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listar(Long usuarioId, Rol rol) {
        return rol == Rol.COMERCIANTE
                ? pedidoRepository.findByComercianteIdOrderByFechaHoraDesc(usuarioId)
                : pedidoRepository.findByAgricultorIdOrderByFechaHoraDesc(usuarioId);
    }

    @Transactional
    public Pedido programarDespacho(Long agricultorId, Long pedidoId, DespachoRequest request) {
        Pedido pedido = buscarPedido(pedidoId);
        if (!pedido.getAgricultor().getId().equals(agricultorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puede gestionar este pedido");
        }
        if (pedido.getEstado() != EstadoPedido.RESERVADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Solo se puede despachar un pedido reservado");
        }

        pedido.setTransportista(request.transportista().trim());
        pedido.setRuta(request.ruta().trim());
        pedido.setFechaEstimadaRecepcion(request.fechaEstimadaRecepcion());
        pedido.setEstado(EstadoPedido.EN_TRANSITO);
        return guardarYNotificarCambio(pedido);
    }

    @Transactional
    public Pedido confirmarRecepcion(Long comercianteId, Long pedidoId) {
        Pedido pedido = buscarPedido(pedidoId);
        if (!pedido.getComerciante().getId().equals(comercianteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puede confirmar este pedido");
        }
        if (pedido.getEstado() != EstadoPedido.EN_TRANSITO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Solo se puede recibir un pedido en tránsito");
        }

        pedido.setEstado(EstadoPedido.ENTREGADO);
        return guardarYNotificarCambio(pedido);
    }

    @Transactional
    public Pedido calificar(Long comercianteId, Long pedidoId, CalificacionRequest request) {
        Pedido pedido = buscarPedido(pedidoId);
        if (!pedido.getComerciante().getId().equals(comercianteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puede calificar este pedido");
        }
        if (pedido.getEstado() != EstadoPedido.ENTREGADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Solo se puede calificar un pedido entregado");
        }

        pedido.setCalificacion(request.calificacion());
        pedido.setComentariosCalificacion(request.comentarios().trim());
        pedido.setEstado(EstadoPedido.FINALIZADO);
        pedidoRepository.saveAndFlush(pedido);
        publicarCambioEstado(pedido);

        Agricultor agricultor = pedido.getAgricultor();
        agricultor.setCalificacionPromedio(
                pedidoRepository.calcularCalificacionPromedio(agricultor.getId()));
        return pedido;
    }

    private Pedido buscarPedido(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    private Pedido guardarYNotificarCambio(Pedido pedido) {
        Pedido guardado = pedidoRepository.save(pedido);
        publicarCambioEstado(guardado);
        return guardado;
    }

    private void publicarCambioEstado(Pedido pedido) {
        eventPublisher.publishEvent(new PedidoEstadoCambiadoEvent(
                pedido.getId(),
                pedido.getComerciante().getId(),
                pedido.getAgricultor().getId(),
                pedido.getEstado()));
    }
}
