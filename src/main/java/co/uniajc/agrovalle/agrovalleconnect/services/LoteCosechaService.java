package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.exceptions.ConflictoException;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoteCosechaService {

    @Autowired

    private LoteCosechaRepository loteCosechaRepository;

    @Autowired

    private AgricultorRepository agricultorRepository;

    public LoteCosecha publicarLote(Long agricultorId, LoteCosecha lote) {

        Agricultor agricultor = agricultorRepository.findById(agricultorId)

                .orElseThrow(() -> new IllegalArgumentException("Agricultor no encontrado"));

        if (lote.getFechaCosecha().isBefore(LocalDate.now())) {

            throw new IllegalArgumentException("La fecha de cosecha no puede ser anterior al dia actual");

        }

        boolean loteDuplicado = loteCosechaRepository
                .existsByAgricultorIdAndTipoProductoIgnoreCaseAndFechaCosechaAndActivoTrue(
                        agricultorId, lote.getTipoProducto(), lote.getFechaCosecha());

        if (loteDuplicado) {
            throw new ConflictoException(
                    "Ya existe un lote activo para ese producto y fecha de cosecha");
        }

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

}
 