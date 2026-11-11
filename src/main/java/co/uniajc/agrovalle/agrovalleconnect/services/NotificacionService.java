package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.exceptions.RecursoNoEncontradoException;
import co.uniajc.agrovalle.agrovalleconnect.models.Notificacion;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.NotificacionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final AgricultorRepository agricultorRepository;

    public NotificacionService(NotificacionRepository notificacionRepository,
                               AgricultorRepository agricultorRepository) {
        this.notificacionRepository = notificacionRepository;
        this.agricultorRepository = agricultorRepository;
    }

    @Transactional(readOnly = true)
    public List<Notificacion> listarPorAgricultor(Long agricultorId) {
        if (!agricultorRepository.existsById(agricultorId)) {
            throw new RecursoNoEncontradoException("Agricultor no encontrado con id " + agricultorId);
        }
        return notificacionRepository.findByAgricultorIdOrderByFechaCreacionDesc(agricultorId);
    }
}
