package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.PrecioPromedioResponse;
import co.uniajc.agrovalle.agrovalleconnect.dto.TransaccionPrecioRequest;
import co.uniajc.agrovalle.agrovalleconnect.exceptions.DatosInsuficientesException;
import co.uniajc.agrovalle.agrovalleconnect.exceptions.RecursoNoEncontradoException;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.TransaccionPrecio;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.TransaccionPrecioRepository;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrecioService {

    private static final int PERIODO_HORAS = 24;
    private static final long MINIMO_TRANSACCIONES = 50;

    private final TransaccionPrecioRepository transaccionPrecioRepository;
    private final AgricultorRepository agricultorRepository;

    public PrecioService(
            TransaccionPrecioRepository transaccionPrecioRepository,
            AgricultorRepository agricultorRepository) {
        this.transaccionPrecioRepository = transaccionPrecioRepository;
        this.agricultorRepository = agricultorRepository;
    }

    @Transactional
    public TransaccionPrecio registrarTransaccion(
            Long agricultorId, TransaccionPrecioRequest solicitud) {
        Agricultor agricultor = agricultorRepository.findById(agricultorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Agricultor no encontrado"));
        TransaccionPrecio transaccion = new TransaccionPrecio();
        transaccion.setProducto(solicitud.producto().trim());
        transaccion.setMunicipio(solicitud.municipio());
        transaccion.setPrecioUnitarioCop(solicitud.precioUnitarioCop());
        transaccion.setRegistradaPor(agricultor);
        return transaccionPrecioRepository.save(transaccion);
    }

    @Transactional(readOnly = true)
    public PrecioPromedioResponse consultarPromedio(String producto) {
        if (producto == null || producto.isBlank()) {
            throw new IllegalArgumentException("El producto es obligatorio");
        }

        Instant hasta = Instant.now();
        Instant desde = hasta.minus(Duration.ofHours(PERIODO_HORAS));
        TransaccionPrecioRepository.PrecioPromedioProjection resumen =
                transaccionPrecioRepository.calcularPromedio(producto.trim(), desde, hasta);
        if (resumen.getCantidad() < MINIMO_TRANSACCIONES) {
            throw new DatosInsuficientesException(
                    "Se requieren al menos 50 transacciones del producto en las últimas 24 horas");
        }

        return new PrecioPromedioResponse(
                producto.trim(),
                resumen.getCantidad(),
                resumen.getPromedio().setScale(2, RoundingMode.HALF_UP),
                PERIODO_HORAS);
    }
}
