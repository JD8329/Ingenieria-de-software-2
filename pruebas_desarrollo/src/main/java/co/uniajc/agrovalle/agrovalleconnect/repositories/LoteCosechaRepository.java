package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository

public interface LoteCosechaRepository extends JpaRepository<LoteCosecha, Long> {

    List<LoteCosecha> findByActivoTrue();

    List<LoteCosecha> findByAgricultorIdAndActivoTrue(Long agricultorId);

    List<LoteCosecha> findByAgricultorIdAndActivoTrueOrderByIdAsc(Long agricultorId);

    List<LoteCosecha> findByTipoProductoIgnoreCaseAndActivoTrue(String tipoProducto);

    @Query("""
            select lote from LoteCosecha lote
            where lote.activo = true
              and (:municipio is null or lote.agricultor.municipio = :municipio)
              and (:categoria is null or lower(lote.categoria) = lower(:categoria))
            """)
    List<LoteCosecha> buscarActivos(
            @Param("municipio") Municipio municipio,
            @Param("categoria") String categoria);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select lote from LoteCosecha lote where lote.id = :loteId")
    Optional<LoteCosecha> buscarPorIdConBloqueo(@Param("loteId") Long loteId);
}
 