package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.ComercianteRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.exceptions.ConflictoException;
import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.ComercianteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ComercianteService {

    private final ComercianteRepository comercianteRepository;
    private final AgricultorRepository agricultorRepository;
    private final PasswordEncoder passwordEncoder;

    public ComercianteService(
            ComercianteRepository comercianteRepository,
            AgricultorRepository agricultorRepository,
            PasswordEncoder passwordEncoder) {
        this.comercianteRepository = comercianteRepository;
        this.agricultorRepository = agricultorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Comerciante registrar(ComercianteRegistroRequest request) {
        String correo = request.correo().trim().toLowerCase(java.util.Locale.ROOT);
        String nit = request.nit().trim().toUpperCase(java.util.Locale.ROOT);
        if (agricultorRepository.existsByCorreoIgnoreCase(correo)
                || comercianteRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ConflictoException("Ya existe un usuario registrado con ese correo");
        }
        if (comercianteRepository.existsByNitIgnoreCase(nit)) {
            throw new ConflictoException("Ya existe un comerciante registrado con ese NIT");
        }

        Comerciante comerciante = new Comerciante();
        comerciante.setNombreCompleto(request.nombreCompleto().trim());
        comerciante.setNombreNegocio(request.nombreNegocio().trim());
        comerciante.setNit(nit);
        comerciante.setCorreo(correo);
        comerciante.setContrasena(passwordEncoder.encode(request.contrasena()));
        comerciante.setTelefono(request.telefono());
        return comercianteRepository.save(comerciante);
    }
}
