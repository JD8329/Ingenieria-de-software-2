package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;

import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;

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
 