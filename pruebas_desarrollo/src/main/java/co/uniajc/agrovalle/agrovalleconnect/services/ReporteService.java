package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.VentasMunicipioResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.repositories.PedidoRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteService {

    private final PedidoRepository pedidoRepository;

    public ReporteService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<VentasMunicipioResponse> ventasPorMunicipio() {
        Map<Municipio, VentasMunicipioResponse> ventas = pedidoRepository
                .calcularVentasPorMunicipio(EstadoPedido.FINALIZADO).stream()
                .collect(Collectors.toMap(VentasMunicipioResponse::municipio, venta -> venta));
        return Arrays.stream(Municipio.values())
                .map(municipio -> ventas.getOrDefault(
                        municipio, new VentasMunicipioResponse(municipio, 0L, 0.0, 0.0)))
                .toList();
    }
}
