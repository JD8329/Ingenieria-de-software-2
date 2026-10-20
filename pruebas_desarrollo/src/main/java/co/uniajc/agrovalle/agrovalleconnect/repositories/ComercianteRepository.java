package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComercianteRepository extends JpaRepository<Comerciante, Long> {

    Optional<Comerciante> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByNitIgnoreCase(String nit);
}
