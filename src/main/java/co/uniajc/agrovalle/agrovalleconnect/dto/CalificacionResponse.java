package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import java.math.BigDecimal;

public record CalificacionResponse(
        Long pedidoId,
        String estado,
        BigDecimal calificacionPromedioAgricultor,
        String comentarioRegistrado) {

    public static CalificacionResponse desde(Pedido pedido, BigDecimal promedio) {
        return new CalificacionResponse(
                pedido.getId(),
                pedido.getEstado().name(),
                promedio,
                pedido.getComentarioCalificacion());
    }
}
