package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.models.CarritoItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    List<CarritoItem> findByComercianteIdOrderByLoteId(Long comercianteId);

    Optional<CarritoItem> findByComercianteIdAndLoteId(Long comercianteId, Long loteId);

    void deleteByComercianteIdAndLoteId(Long comercianteId, Long loteId);

    void deleteByComercianteId(Long comercianteId);
}
