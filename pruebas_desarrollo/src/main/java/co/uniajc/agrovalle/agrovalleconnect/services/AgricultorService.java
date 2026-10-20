package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.AgricultorRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.LoteCosechaResponse;
import co.uniajc.agrovalle.agrovalleconnect.dto.PerfilAgricultorResponse;
import co.uniajc.agrovalle.agrovalleconnect.exceptions.ConflictoException;
import co.uniajc.agrovalle.agrovalleconnect.exceptions.RecursoNoEncontradoException;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.ComercianteRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.PedidoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AgricultorService {

    @Autowired
    private AgricultorRepository agricultorRepository;

    @Autowired
    private ComercianteRepository comercianteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoteCosechaRepository loteCosechaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    public Agricultor registrarAgricultor(AgricultorRegistroRequest request) {
        String correo = request.correo().trim().toLowerCase(java.util.Locale.ROOT);
        if (agricultorRepository.existsByCorreoIgnoreCase(correo)
                || comercianteRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ConflictoException("Ya existe un usuario registrado con ese correo");
        }

        if (agricultorRepository.existsByCedula(request.cedula())) {
            throw new ConflictoException("Ya existe un agricultor registrado con esa cedula");
        }

        Agricultor agricultor = new Agricultor();
        agricultor.setNombreCompleto(request.nombreCompleto());
        agricultor.setCedula(request.cedula());
        agricultor.setCorreo(correo);
        agricultor.setContrasena(passwordEncoder.encode(request.contrasena()));
        agricultor.setTelefono(request.telefono());
        agricultor.setUbicacionFinca(request.ubicacionFinca());
        agricultor.setMunicipio(request.municipio());
        agricultor.setRol(Rol.PRODUCCION);
        return agricultorRepository.save(agricultor);
    }

    public PerfilAgricultorResponse obtenerPerfilPublico(Long agricultorId) {
        Agricultor agricultor = agricultorRepository.findById(agricultorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Agricultor no encontrado"));
        Double calificacion = pedidoRepository.calcularCalificacionPromedio(agricultorId);
        List<LoteCosechaResponse> lotes = loteCosechaRepository
                .findByAgricultorIdAndActivoTrueOrderByIdAsc(agricultorId).stream()
                .map(LoteCosechaResponse::desde)
                .toList();
        return new PerfilAgricultorResponse(
                agricultor.getId(),
                agricultor.getNombreCompleto(),
                agricultor.getUbicacionFinca(),
                agricultor.getMunicipio(),
                calificacion == null ? 0.0 : calificacion,
                lotes);
    }
}