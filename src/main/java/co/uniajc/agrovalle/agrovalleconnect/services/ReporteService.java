package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.ReporteVentasMunicipioResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import co.uniajc.agrovalle.agrovalleconnect.repositories.PedidoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteService {

    private final PedidoRepository pedidoRepository;

    public ReporteService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<ReporteVentasMunicipioResponse> ventasPorMunicipio() {
        Map<Municipio, AcumuladoMunicipio> acumulados = new LinkedHashMap<>();

        for (Pedido pedido : pedidoRepository.findAll()) {
            Map<Municipio, AcumuladoMunicipio> porPedido = new LinkedHashMap<>();
            pedido.getDetalles().forEach(detalle -> {
                Municipio municipio = detalle.getLote().getAgricultor().getMunicipio();
                AcumuladoMunicipio acumulado = porPedido.computeIfAbsent(
                        municipio, clave -> new AcumuladoMunicipio());
                acumulado.cantidadKg = acumulado.cantidadKg.add(detalle.getCantidadKg());
                acumulado.totalVentas = acumulado.totalVentas.add(detalle.getSubtotal());
                acumulado.pedidos.add(pedido.getId());
            });

            porPedido.forEach((municipio, acumuladoPedido) -> {
                AcumuladoMunicipio acumulado = acumulados.computeIfAbsent(
                        municipio, clave -> new AcumuladoMunicipio());
                acumulado.cantidadKg = acumulado.cantidadKg.add(acumuladoPedido.cantidadKg);
                acumulado.totalVentas = acumulado.totalVentas.add(acumuladoPedido.totalVentas);
                acumulado.pedidos.addAll(acumuladoPedido.pedidos);
            });
        }

        return acumulados.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ReporteVentasMunicipioResponse(
                        entry.getKey(),
                        entry.getValue().pedidos.size(),
                        entry.getValue().cantidadKg.setScale(2, RoundingMode.HALF_UP),
                        entry.getValue().totalVentas.setScale(2, RoundingMode.HALF_UP)))
                .toList();
    }

    private static class AcumuladoMunicipio {
        private BigDecimal cantidadKg = BigDecimal.ZERO;
        private BigDecimal totalVentas = BigDecimal.ZERO;
        private final Set<Long> pedidos = new TreeSet<>();
    }
}
