package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.events.LoteCosechaPublicadoEvent;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoteCosechaService {

    private final LoteCosechaRepository loteCosechaRepository;

    private final AgricultorRepository agricultorRepository;

    private final ApplicationEventPublisher eventPublisher;

    public LoteCosechaService(
            LoteCosechaRepository loteCosechaRepository,
            AgricultorRepository agricultorRepository,
            ApplicationEventPublisher eventPublisher) {
        this.loteCosechaRepository = loteCosechaRepository;
        this.agricultorRepository = agricultorRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public LoteCosecha publicarLote(Long agricultorId, LoteCosecha lote) {
        Agricultor agricultor = agricultorRepository.findById(agricultorId)
                .orElseThrow(() -> new IllegalArgumentException("Agricultor no encontrado"));

        if (lote.getFechaCosecha().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha de cosecha no puede ser anterior al dia actual");
        }

        lote.setAgricultor(agricultor);
        lote.setActivo(true);

        LoteCosecha loteGuardado = loteCosechaRepository.save(lote);
        eventPublisher.publishEvent(new LoteCosechaPublicadoEvent(
                loteGuardado.getId(), agricultor.getId(), loteGuardado.getTipoProducto()));
        return loteGuardado;
    }

    public List<LoteCosecha> listarLotesActivos() {
        return loteCosechaRepository.findByActivoTrue();
    }

    public List<LoteCosecha> listarLotesPorAgricultor(Long agricultorId) {
        return loteCosechaRepository.findByAgricultorIdAndActivoTrue(agricultorId);
    }
}
 