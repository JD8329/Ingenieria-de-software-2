package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.dto.PrecioPromedioDatos;
import co.uniajc.agrovalle.agrovalleconnect.models.Transaccion;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    @Query("""
            select new co.uniajc.agrovalle.agrovalleconnect.dto.PrecioPromedioDatos(
                count(transaccion), avg(transaccion.precioUnitario))
            from Transaccion transaccion
            where lower(transaccion.lote.tipoProducto) = lower(:producto)
              and transaccion.fechaHora >= :desde
            """)
    PrecioPromedioDatos calcularPrecioPromedio(
            @Param("producto") String producto,
            @Param("desde") Instant desde);
}
