package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.models.Notificacion;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByDestinatarioIdAndDestinatarioRolOrderByFechaHoraDesc(
            Long destinatarioId,
            Rol destinatarioRol);

    Optional<Notificacion> findByIdAndDestinatarioIdAndDestinatarioRol(
            Long id,
            Long destinatarioId,
            Rol destinatarioRol);
}
