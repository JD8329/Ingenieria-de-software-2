package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoteCosechaRepository extends JpaRepository<LoteCosecha, Long> {

    List<LoteCosecha> findByActivoTrue();

    List<LoteCosecha> findByAgricultorIdAndActivoTrue(Long agricultorId);
}
