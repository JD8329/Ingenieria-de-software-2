package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.models.Mensaje;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    List<Mensaje> findByLoteIdOrderByFechaHoraAsc(Long loteId);

    List<Mensaje> findByLoteIdAndComercianteIdOrderByFechaHoraAsc(Long loteId, Long comercianteId);
}
