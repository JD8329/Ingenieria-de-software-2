package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.AgricultorRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AgricultorService {

    @Autowired
    private AgricultorRepository agricultorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Agricultor registrarAgricultor(AgricultorRegistroRequest request) {
        if (agricultorRepository.existsByCorreo(request.correo())) {
            throw new IllegalArgumentException("Ya existe un agricultor registrado con ese correo");
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