package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository

public interface LoteCosechaRepository extends JpaRepository<LoteCosecha, Long> {

    List<LoteCosecha> findByActivoTrue();

    List<LoteCosecha> findByAgricultorIdAndActivoTrue(Long agricultorId);

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
 