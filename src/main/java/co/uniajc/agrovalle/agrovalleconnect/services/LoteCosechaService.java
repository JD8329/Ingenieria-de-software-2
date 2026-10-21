package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

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

        lote.setAgricultor(agricultor);

        if (lote.getCategoria() != null) {
            String categoria = lote.getCategoria().trim();
            if (categoria.isEmpty()) {
                throw new IllegalArgumentException("La categoría no puede estar vacía");
            }
            lote.setCategoria(categoria);
        }

        lote.setActivo(true);

        return loteCosechaRepository.save(lote);

    }

    public List<LoteCosecha> listarLotesActivos() {

        return loteCosechaRepository.findByActivoTrue();

    }

    public List<LoteCosecha> listarLotesPorAgricultor(Long agricultorId) {

        return loteCosechaRepository.findByAgricultorIdAndActivoTrue(agricultorId);

    }

    public List<LoteCosecha> buscarLotes(Municipio municipio, String categoria) {
        String categoriaNormalizada = categoria == null ? null : categoria.trim();
        if (municipio == null && categoriaNormalizada == null) {
            throw new IllegalArgumentException("Debe indicar el municipio, la categoría o ambos");
        }
        if (categoriaNormalizada != null && categoriaNormalizada.isEmpty()) {
            throw new IllegalArgumentException("La categoría no puede estar vacía");
        }
        return loteCosechaRepository.buscarActivos(municipio, categoriaNormalizada);
    }

}
 