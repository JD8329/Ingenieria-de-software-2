package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.MensajeRequest;
import co.uniajc.agrovalle.agrovalleconnect.exceptions.RecursoNoEncontradoException;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Mensaje;
import co.uniajc.agrovalle.agrovalleconnect.models.Notificacion;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.MensajeRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.NotificacionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final LoteCosechaRepository loteCosechaRepository;
    private final NotificacionRepository notificacionRepository;

    public MensajeService(MensajeRepository mensajeRepository,
                          LoteCosechaRepository loteCosechaRepository,
                          NotificacionRepository notificacionRepository) {
        this.mensajeRepository = mensajeRepository;
        this.loteCosechaRepository = loteCosechaRepository;
        this.notificacionRepository = notificacionRepository;
    }

    @Transactional
    public Mensaje enviar(MensajeRequest datos) {
        LoteCosecha lote = loteCosechaRepository.findById(datos.loteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Lote no encontrado con id " + datos.loteId()));

        Mensaje mensaje = new Mensaje();
        mensaje.setLote(lote);
        mensaje.setComercianteId(datos.comercianteId());
        mensaje.setContenido(datos.contenido().trim());
        Mensaje guardado = mensajeRepository.save(mensaje);

        Notificacion notificacion = new Notificacion();
        notificacion.setAgricultor(lote.getAgricultor());
        notificacion.setTipo("MENSAJE");
        notificacion.setMensaje("Nuevo mensaje de un comerciante para el lote " + lote.getId());
        notificacion.setReferenciaId(guardado.getId());
        notificacionRepository.save(notificacion);
        return guardado;
    }

    @Transactional(readOnly = true)
    public List<Mensaje> historial(Long loteId, Long comercianteId) {
        if (!loteCosechaRepository.existsById(loteId)) {
            throw new RecursoNoEncontradoException("Lote no encontrado con id " + loteId);
        }
        return mensajeRepository.findByLoteIdAndComercianteIdOrderByFechaCreacionAsc(loteId, comercianteId);
    }
}
