package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgricultorService {

    @Autowired
    private AgricultorRepository agricultorRepository;

    public Agricultor registrarAgricultor(Agricultor agricultor) {
        if (agricultorRepository.existsByCorreo(agricultor.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un agricultor registrado con ese correo");
        }

        if (agricultorRepository.existsByCedula(agricultor.getCedula())) {
            throw new IllegalArgumentException("Ya existe un agricultor registrado con esa cedula");
        }

        return agricultorRepository.save(agricultor);
    }
}