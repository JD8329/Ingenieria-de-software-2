package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.models.Notificacion;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.models.TipoNotificacion;
import co.uniajc.agrovalle.agrovalleconnect.repositories.NotificacionRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public void crear(Long destinatarioId, Rol rol, TipoNotificacion tipo, String contenido) {
        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatarioId(destinatarioId);
        notificacion.setDestinatarioRol(rol);
        notificacion.setTipo(tipo);
        notificacion.setContenido(contenido);
        notificacion.setFechaHora(Instant.now());
        notificacion.setLeida(false);
        notificacionRepository.save(notificacion);
    }

    @Transactional(readOnly = true)
    public List<Notificacion> listar(Long usuarioId, Rol rol) {
        return notificacionRepository
                .findByDestinatarioIdAndDestinatarioRolOrderByFechaHoraDesc(usuarioId, rol);
    }

    @Transactional
    public Notificacion marcarLeida(Long usuarioId, Rol rol, Long notificacionId) {
        Notificacion notificacion = notificacionRepository
                .findByIdAndDestinatarioIdAndDestinatarioRol(notificacionId, usuarioId, rol)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Notificacion no encontrada"));
        notificacion.setLeida(true);
        return notificacion;
    }
}
