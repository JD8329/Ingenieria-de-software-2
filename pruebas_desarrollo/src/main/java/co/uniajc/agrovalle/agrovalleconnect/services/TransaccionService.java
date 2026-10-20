package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.PrecioPromedioDatos;
import co.uniajc.agrovalle.agrovalleconnect.dto.PrecioPromedioResponse;
import co.uniajc.agrovalle.agrovalleconnect.dto.TransaccionRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Transaccion;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.TransaccionRepository;
import java.time.Duration;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransaccionService {

    private static final long MINIMO_TRANSACCIONES = 50;
    private static final Duration VENTANA_PROMEDIO = Duration.ofHours(24);

    private final LoteCosechaRepository loteCosechaRepository;
    private final TransaccionRepository transaccionRepository;

    public TransaccionService(
            LoteCosechaRepository loteCosechaRepository,
            TransaccionRepository transaccionRepository) {
        this.loteCosechaRepository = loteCosechaRepository;
        this.transaccionRepository = transaccionRepository;
    }

    @Transactional
    public Transaccion registrar(
            Long agricultorId,
            Long loteId,
            TransaccionRegistroRequest request) {
        LoteCosecha lote = loteCosechaRepository.buscarPorIdConBloqueo(loteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote no encontrado"));
        if (!lote.getAgricultor().getId().equals(agricultorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El lote no pertenece al agricultor autenticado");
        }
        if (!Boolean.TRUE.equals(lote.getActivo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se pueden registrar ventas en un lote inactivo");
        }
        if (request.cantidadKg() > lote.getCantidadKg()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La cantidad vendida supera el stock disponible");
        }

        lote.setCantidadKg(lote.getCantidadKg() - request.cantidadKg());
        if (lote.getCantidadKg() == 0) {
            lote.setActivo(false);
        }
        loteCosechaRepository.save(lote);

        Transaccion transaccion = new Transaccion();
        transaccion.setLote(lote);
        transaccion.setCantidadKg(request.cantidadKg());
        transaccion.setPrecioUnitario(request.precioUnitario());
        transaccion.setFechaHora(Instant.now());
        return transaccionRepository.save(transaccion);
    }

    @Transactional(readOnly = true)
    public PrecioPromedioResponse calcularPrecioPromedio(String producto) {
        if (producto == null || producto.isBlank()) {
            throw new IllegalArgumentException("El producto es obligatorio");
        }
        Instant hasta = Instant.now();
        Instant desde = hasta.minus(VENTANA_PROMEDIO);
        PrecioPromedioDatos datos = transaccionRepository.calcularPrecioPromedio(producto.trim(), desde);
        if (datos.cantidadTransacciones() < MINIMO_TRANSACCIONES) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_CONTENT,
                    "Se requieren al menos 50 transacciones del producto en las ultimas 24 horas");
        }
        return new PrecioPromedioResponse(
                producto.trim(),
                datos.cantidadTransacciones(),
                datos.precioPromedio(),
                "COP",
                desde,
                hasta);
    }
}
