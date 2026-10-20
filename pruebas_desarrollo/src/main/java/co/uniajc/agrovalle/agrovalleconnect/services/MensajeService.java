package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.MensajeRequest;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Mensaje;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.models.TipoNotificacion;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.ComercianteRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.MensajeRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final LoteCosechaRepository loteCosechaRepository;
    private final ComercianteRepository comercianteRepository;
    private final AgricultorRepository agricultorRepository;
    private final NotificacionService notificacionService;

    public MensajeService(
            MensajeRepository mensajeRepository,
            LoteCosechaRepository loteCosechaRepository,
            ComercianteRepository comercianteRepository,
            AgricultorRepository agricultorRepository,
            NotificacionService notificacionService) {
        this.mensajeRepository = mensajeRepository;
        this.loteCosechaRepository = loteCosechaRepository;
        this.comercianteRepository = comercianteRepository;
        this.agricultorRepository = agricultorRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public Mensaje enviar(Long comercianteId, MensajeRequest request) {
        Comerciante comerciante = comercianteRepository.findById(comercianteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Comerciante no encontrado"));
        LoteCosecha lote = loteCosechaRepository.findById(request.loteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote no encontrado"));
        if (!Boolean.TRUE.equals(lote.getActivo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede contactar por un lote inactivo");
        }
        Agricultor agricultor = agricultorRepository.findById(lote.getAgricultor().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Agricultor no encontrado"));

        Mensaje mensaje = new Mensaje();
        mensaje.setLote(lote);
        mensaje.setComerciante(comerciante);
        mensaje.setAgricultor(agricultor);
        mensaje.setContenido(request.contenido().trim());
        mensaje.setFechaHora(Instant.now());
        Mensaje guardado = mensajeRepository.save(mensaje);
        notificacionService.crear(
                agricultor.getId(),
                Rol.PRODUCCION,
                TipoNotificacion.MENSAJE_NUEVO,
                "Nuevo mensaje sobre el lote " + lote.getId() + " de " + comerciante.getNombreNegocio());
        return guardado;
    }

    @Transactional(readOnly = true)
    public List<Mensaje> listar(Long loteId, Long usuarioId, Rol rol) {
        if (rol == Rol.COMERCIANTE) {
            List<Mensaje> mensajes = mensajeRepository
                    .findByLoteIdAndComercianteIdOrderByFechaHoraAsc(loteId, usuarioId);
            if (mensajes.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "No puede consultar esta conversacion");
            }
            return mensajes;
        }

        LoteCosecha lote = loteCosechaRepository.findById(loteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote no encontrado"));
        if (!lote.getAgricultor().getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puede consultar esta conversacion");
        }
        return mensajeRepository.findByLoteIdOrderByFechaHoraAsc(loteId);
    }
}
