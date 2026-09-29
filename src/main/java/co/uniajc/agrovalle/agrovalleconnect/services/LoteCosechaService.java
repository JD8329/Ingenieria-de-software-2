package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.LoteCosechaRequest;
import co.uniajc.agrovalle.agrovalleconnect.exceptions.RecursoNoEncontradoException;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LoteCosechaService {

    private final LoteCosechaRepository loteCosechaRepository;
    private final AgricultorRepository agricultorRepository;

    public LoteCosechaService(LoteCosechaRepository loteCosechaRepository,
                              AgricultorRepository agricultorRepository) {
        this.loteCosechaRepository = loteCosechaRepository;
        this.agricultorRepository = agricultorRepository;
    }

    public LoteCosecha publicar(Long agricultorId, LoteCosechaRequest datos) {
        Agricultor agricultor = agricultorRepository.findById(agricultorId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Agricultor no encontrado con id " + agricultorId));

        if (datos.fechaCosecha().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de cosecha no puede ser anterior al dia actual");
        }

        LoteCosecha lote = new LoteCosecha();
        lote.setTipoProducto(datos.tipoProducto());
        lote.setCategoria(datos.categoria());
        lote.setCantidadKg(datos.cantidadKg());
        lote.setPrecioUnitario(datos.precioUnitario());
        lote.setFechaCosecha(datos.fechaCosecha());
        lote.setAgricultor(agricultor);
        lote.setActivo(true);
        return loteCosechaRepository.save(lote);
    }

    public List<LoteCosecha> listarActivos() {
        return loteCosechaRepository.findByActivoTrue();
    }

    public List<LoteCosecha> listarPorAgricultor(Long agricultorId) {
        return loteCosechaRepository.findByAgricultorIdAndActivoTrue(agricultorId);
    }
}
