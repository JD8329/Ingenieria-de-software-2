package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.models.TransaccionPrecio;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransaccionPrecioRepository extends JpaRepository<TransaccionPrecio, Long> {

    @Query("""
            select count(t) as cantidad, avg(t.precioUnitarioCop) as promedio
            from TransaccionPrecio t
            where lower(t.producto) = lower(:producto)
              and t.registradaEn >= :desde
              and t.registradaEn <= :hasta
            """)
    PrecioPromedioProjection calcularPromedio(
            @Param("producto") String producto,
            @Param("desde") Instant desde,
            @Param("hasta") Instant hasta);

    interface PrecioPromedioProjection {
        long getCantidad();

        java.math.BigDecimal getPromedio();
    }
}
