package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.ComercianteRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.exceptions.ConflictoException;
import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.ComercianteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ComercianteService {

    private final AgricultorRepository agricultorRepository;

    private final ComercianteRepository comercianteRepository;

    private final PasswordEncoder passwordEncoder;

    public ComercianteService(
            AgricultorRepository agricultorRepository,
            ComercianteRepository comercianteRepository,
            PasswordEncoder passwordEncoder) {
        this.agricultorRepository = agricultorRepository;
        this.comercianteRepository = comercianteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Comerciante registrar(ComercianteRegistroRequest request) {
        if (agricultorRepository.existsByCorreo(request.correo())
                || comercianteRepository.existsByCorreo(request.correo())) {
            throw new ConflictoException("Ya existe una cuenta registrada con ese correo");
        }
        if (comercianteRepository.existsByNit(request.nit())) {
            throw new ConflictoException("Ya existe un comerciante registrado con ese NIT");
        }

        Comerciante comerciante = new Comerciante();
        comerciante.setRazonSocial(request.razonSocial());
        comerciante.setNit(request.nit());
        comerciante.setCorreo(request.correo());
        comerciante.setContrasena(passwordEncoder.encode(request.contrasena()));
        comerciante.setTelefono(request.telefono());
        comerciante.setDireccion(request.direccion());
        comerciante.setMunicipio(request.municipio());
        return comercianteRepository.save(comerciante);
    }
}
