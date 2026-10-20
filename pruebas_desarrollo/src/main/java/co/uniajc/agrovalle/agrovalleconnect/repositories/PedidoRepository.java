package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.dto.VentasMunicipioResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByComercianteIdOrderByFechaHoraDesc(Long comercianteId);

    List<Pedido> findByAgricultorIdOrderByFechaHoraDesc(Long agricultorId);

    @Query("""
            select case when count(pedido) > 0 then true else false end
            from Pedido pedido
            join pedido.items item
            where item.lote.id = :loteId
              and pedido.estado in :estados
            """)
    boolean existePedidoActivoPorLote(
            @Param("loteId") Long loteId,
            @Param("estados") List<EstadoPedido> estados);

    @Query("""
            select new co.uniajc.agrovalle.agrovalleconnect.dto.VentasMunicipioResponse(
                pedido.agricultor.municipio,
                count(distinct pedido.id),
                sum(item.cantidadKg),
                sum(item.cantidadKg * item.precioUnitario))
            from Pedido pedido
            join pedido.items item
            where pedido.estado = :estado
            group by pedido.agricultor.municipio
            """)
    List<VentasMunicipioResponse> calcularVentasPorMunicipio(
            @Param("estado") EstadoPedido estado);

    @Query("""
            select avg(p.calificacion)
            from Pedido p
            where p.agricultor.id = :agricultorId and p.calificacion is not null
            """)
    Double calcularCalificacionPromedio(@Param("agricultorId") Long agricultorId);
}
