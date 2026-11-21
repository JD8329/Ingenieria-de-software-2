package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("""
            select case when count(p) > 0 then true else false end
            from Pedido p join p.detalles d
            where d.lote.id = :loteId and p.estado <> :estadoFinalizado
            """)
    boolean existePedidoActivoParaLote(
            @Param("loteId") Long loteId,
            @Param("estadoFinalizado") EstadoPedido estadoFinalizado);

    @Query("""
            select p.calificacion from Pedido p
            where p.calificacion is not null
              and exists (
                  select d.id from DetallePedido d
                  where d.pedido = p and d.lote.agricultor.id = :agricultorId
              )
            """)
    List<Integer> findCalificacionesByAgricultorId(
            @Param("agricultorId") Long agricultorId);
}
