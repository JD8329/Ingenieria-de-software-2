package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.AgricultorRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.ComercianteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AgricultorService {

    private final AgricultorRepository agricultorRepository;

    private final ComercianteRepository comercianteRepository;

    private final PasswordEncoder passwordEncoder;

    public AgricultorService(
            AgricultorRepository agricultorRepository,
            ComercianteRepository comercianteRepository,
            PasswordEncoder passwordEncoder) {
        this.agricultorRepository = agricultorRepository;
        this.comercianteRepository = comercianteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Agricultor registrarAgricultor(AgricultorRegistroRequest request) {
        if (agricultorRepository.existsByCorreo(request.correo())
                || comercianteRepository.existsByCorreo(request.correo())) {
            throw new IllegalArgumentException("Ya existe una cuenta registrada con ese correo");
        }

        if (agricultorRepository.existsByCedula(request.cedula())) {
            throw new IllegalArgumentException("Ya existe un agricultor registrado con esa cedula");
        }

        Agricultor agricultor = new Agricultor();
        agricultor.setNombreCompleto(request.nombreCompleto());
        agricultor.setCedula(request.cedula());
        agricultor.setCorreo(request.correo());
        agricultor.setContrasena(passwordEncoder.encode(request.contrasena()));
        agricultor.setTelefono(request.telefono());
        agricultor.setUbicacionFinca(request.ubicacionFinca());
        agricultor.setMunicipio(request.municipio());
        agricultor.setRol(Rol.PRODUCCION);
        return agricultorRepository.save(agricultor);
    }
}
