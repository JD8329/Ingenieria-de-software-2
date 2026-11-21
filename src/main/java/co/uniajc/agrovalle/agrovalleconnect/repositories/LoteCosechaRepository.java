package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteCosechaRepository extends JpaRepository<LoteCosecha, Long> {

    List<LoteCosecha> findByActivoTrue();

    List<LoteCosecha> findByAgricultorIdAndActivoTrue(Long agricultorId);

    boolean existsByAgricultorIdAndTipoProductoIgnoreCaseAndFechaCosechaAndActivoTrue(
            Long agricultorId, String tipoProducto, LocalDate fechaCosecha);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from LoteCosecha l where l.id = :id")
    Optional<LoteCosecha> findByIdForUpdate(@Param("id") Long id);

    List<LoteCosecha> findByTipoProductoIgnoreCaseAndActivoTrue(String tipoProducto);

    @Query("""
            select l from LoteCosecha l
            join fetch l.agricultor a
            where l.activo = true
              and (:municipio is null or a.municipio = :municipio)
              and (:categoria is null or lower(l.categoria) = lower(:categoria))
            order by l.fechaCosecha, l.id
            """)
    List<LoteCosecha> buscarActivos(
            @Param("municipio") Municipio municipio,
            @Param("categoria") String categoria);
}
 
