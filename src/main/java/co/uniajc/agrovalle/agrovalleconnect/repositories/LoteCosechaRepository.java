package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoteCosechaRepository extends JpaRepository<LoteCosecha, Long> {

    List<LoteCosecha> findByActivoTrue();

    List<LoteCosecha> findByAgricultorIdAndActivoTrue(Long agricultorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select lote from LoteCosecha lote where lote.id = :id")
    Optional<LoteCosecha> findByIdForUpdate(@Param("id") Long id);
}
