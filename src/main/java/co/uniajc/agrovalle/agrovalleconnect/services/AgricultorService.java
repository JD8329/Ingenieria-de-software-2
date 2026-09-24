package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.AgricultorRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.exceptions.ConflictoException;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AgricultorService {

    private final AgricultorRepository agricultorRepository;
    private final PasswordEncoder passwordEncoder;

    public AgricultorService(AgricultorRepository agricultorRepository, PasswordEncoder passwordEncoder) {
        this.agricultorRepository = agricultorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Agricultor registrar(AgricultorRegistroRequest datos) {
        if (agricultorRepository.existsByCorreo(datos.correo())) {
            throw new ConflictoException("Ya existe un agricultor registrado con ese correo");
        }
        if (agricultorRepository.existsByCedula(datos.cedula())) {
            throw new ConflictoException("Ya existe un agricultor registrado con esa cedula");
        }

        Agricultor agricultor = new Agricultor();
        agricultor.setNombreCompleto(datos.nombreCompleto());
        agricultor.setCedula(datos.cedula());
        agricultor.setCorreo(datos.correo());
        agricultor.setContrasena(passwordEncoder.encode(datos.contrasena()));
        agricultor.setTelefono(datos.telefono());
        agricultor.setNombreFinca(datos.nombreFinca());
        agricultor.setMunicipio(datos.municipio());
        agricultor.setRol(Rol.AGRICULTOR);
        return agricultorRepository.save(agricultor);
    }
}
