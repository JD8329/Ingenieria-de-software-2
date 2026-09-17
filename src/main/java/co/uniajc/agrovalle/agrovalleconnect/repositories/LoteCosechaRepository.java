package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface LoteCosechaRepository extends JpaRepository<LoteCosecha, Long> {

    List<LoteCosecha> findByActivoTrue();

    List<LoteCosecha> findByAgricultorIdAndActivoTrue(Long agricultorId);

    List<LoteCosecha> findByTipoProductoIgnoreCaseAndActivoTrue(String tipoProducto);

}
 