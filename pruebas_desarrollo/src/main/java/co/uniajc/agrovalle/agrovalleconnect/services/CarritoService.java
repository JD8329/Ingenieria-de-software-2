package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.CarritoItemRequest;
import co.uniajc.agrovalle.agrovalleconnect.models.CarritoItem;
import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.repositories.CarritoItemRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.ComercianteRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CarritoService {

    private final CarritoItemRepository carritoItemRepository;
    private final ComercianteRepository comercianteRepository;
    private final LoteCosechaRepository loteCosechaRepository;

    public CarritoService(
            CarritoItemRepository carritoItemRepository,
            ComercianteRepository comercianteRepository,
            LoteCosechaRepository loteCosechaRepository) {
        this.carritoItemRepository = carritoItemRepository;
        this.comercianteRepository = comercianteRepository;
        this.loteCosechaRepository = loteCosechaRepository;
    }

    @Transactional(readOnly = true)
    public List<CarritoItem> listar(Long comercianteId) {
        return carritoItemRepository.findByComercianteIdOrderByLoteId(comercianteId);
    }

    @Transactional
    public CarritoItem agregar(Long comercianteId, CarritoItemRequest request) {
        Comerciante comerciante = comercianteRepository.findById(comercianteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Comerciante no encontrado"));
        LoteCosecha lote = loteCosechaRepository.buscarPorIdConBloqueo(request.loteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote no encontrado"));
        if (!Boolean.TRUE.equals(lote.getActivo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El lote no esta disponible");
        }

        CarritoItem item = carritoItemRepository.findByComercianteIdAndLoteId(comercianteId, lote.getId())
                .orElseGet(() -> {
                    CarritoItem nuevo = new CarritoItem();
                    nuevo.setComerciante(comerciante);
                    nuevo.setLote(lote);
                    nuevo.setCantidadKg(0.0);
                    return nuevo;
                });
        double nuevaCantidad = item.getCantidadKg() + request.cantidadKg();
        if (nuevaCantidad > lote.getCantidadKg()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "La cantidad solicitada supera el stock disponible");
        }
        item.setCantidadKg(nuevaCantidad);
        return carritoItemRepository.save(item);
    }

    @Transactional
    public void retirar(Long comercianteId, Long loteId) {
        if (carritoItemRepository.findByComercianteIdAndLoteId(comercianteId, loteId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El lote no esta en el carrito");
        }
        carritoItemRepository.deleteByComercianteIdAndLoteId(comercianteId, loteId);
    }
}
