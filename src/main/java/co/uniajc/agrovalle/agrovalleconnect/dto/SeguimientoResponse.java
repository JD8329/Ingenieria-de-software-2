package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import java.time.LocalDateTime;
import java.util.List;

public record SeguimientoResponse(
        Long pedidoId,
        String estado,
        String transportadora,
        String conductor,
        String vehiculo,
        LocalDateTime fechaEstimadaEntrega,
        List<String> ruta,
        String observaciones) {

    public static SeguimientoResponse desde(Pedido pedido) {
        List<String> ruta = pedido.getRuta() == null || pedido.getRuta().isBlank()
                ? List.of()
                : List.of(pedido.getRuta().split(" -> "));
        return new SeguimientoResponse(
                pedido.getId(),
                pedido.getEstado().name(),
                pedido.getTransportadora(),
                pedido.getConductor(),
                pedido.getVehiculo(),
                pedido.getFechaEstimadaEntrega(),
                ruta,
                pedido.getObservaciones());
    }
}
