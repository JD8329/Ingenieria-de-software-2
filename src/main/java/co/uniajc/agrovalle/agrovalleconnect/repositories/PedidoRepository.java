package co.uniajc.agrovalle.agrovalleconnect.repositories;

import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

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
