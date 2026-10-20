package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.LoteCosechaRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.LoteCosechaActualizacionRequest;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.PedidoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LoteCosechaService {

    private final LoteCosechaRepository loteCosechaRepository;
    private final AgricultorRepository agricultorRepository;
    private final PedidoRepository pedidoRepository;

    public LoteCosechaService(
            LoteCosechaRepository loteCosechaRepository,
            AgricultorRepository agricultorRepository,
            PedidoRepository pedidoRepository) {
        this.loteCosechaRepository = loteCosechaRepository;
        this.agricultorRepository = agricultorRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public LoteCosecha actualizarLote(
            Long agricultorId, Long loteId, LoteCosechaActualizacionRequest request) {
        if (request.cantidadKg() == null && request.precioUnitario() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Debe indicar la cantidad o el precio para actualizar");
        }
        LoteCosecha lote = buscarLotePropio(agricultorId, loteId);
        validarSinPedidosActivos(loteId);
        if (request.cantidadKg() != null) {
            lote.setCantidadKg(request.cantidadKg());
        }
        if (request.precioUnitario() != null) {
            lote.setPrecioUnitario(request.precioUnitario());
        }
        return loteCosechaRepository.save(lote);
    }

    @Transactional
    public LoteCosecha desactivarLote(Long agricultorId, Long loteId) {
        LoteCosecha lote = buscarLotePropio(agricultorId, loteId);
        validarSinPedidosActivos(loteId);
        lote.setActivo(false);
        return loteCosechaRepository.save(lote);
    }

    private LoteCosecha buscarLotePropio(Long agricultorId, Long loteId) {
        LoteCosecha lote = loteCosechaRepository.buscarPorIdConBloqueo(loteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lote no encontrado"));
        if (!lote.getAgricultor().getId().equals(agricultorId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "El lote no pertenece al agricultor autenticado");
        }
        return lote;
    }

    private void validarSinPedidosActivos(Long loteId) {
        if (pedidoRepository.existePedidoActivoPorLote(
                loteId, List.of(
                        EstadoPedido.RESERVADO,
                        EstadoPedido.EN_TRANSITO,
                        EstadoPedido.ENTREGADO))) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El lote tiene pedidos activos y no se puede modificar");
        }
    }

    public LoteCosecha publicarLote(Long agricultorId, LoteCosechaRequest request) {
        Agricultor agricultor = agricultorRepository.findById(agricultorId)
                .orElseThrow(() -> new IllegalArgumentException("Agricultor no encontrado"));
        if (request.fechaCosecha().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de cosecha no puede ser anterior al dia actual");
        }

        LoteCosecha lote = new LoteCosecha();
        lote.setTipoProducto(request.tipoProducto());
        lote.setCategoria(request.categoria());
        lote.setCantidadKg(request.cantidadKg());
        lote.setPrecioUnitario(request.precioUnitario());
        lote.setFechaCosecha(request.fechaCosecha());
        lote.setAgricultor(agricultor);
        lote.setActivo(true);
        return loteCosechaRepository.save(lote);
    }

    public List<LoteCosecha> listarLotesActivos() {
        return loteCosechaRepository.findByActivoTrue();
    }

    public List<LoteCosecha> listarLotesPorAgricultor(Long agricultorId) {
        return loteCosechaRepository.findByAgricultorIdAndActivoTrue(agricultorId);
    }

    public List<LoteCosecha> buscarLotesActivos(String municipio, String categoria) {
        Municipio municipioBuscado = null;
        if (municipio != null && !municipio.isBlank()) {
            try {
                municipioBuscado = Municipio.valueOf(municipio.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("Municipio no valido: " + municipio, exception);
            }
        }
        String categoriaBuscada = categoria == null || categoria.isBlank() ? null : categoria.trim();
        return loteCosechaRepository.buscarActivos(municipioBuscado, categoriaBuscada);
    }
}
 