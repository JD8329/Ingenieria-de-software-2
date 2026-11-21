package co.uniajc.agrovalle.agrovalleconnect.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import co.uniajc.agrovalle.agrovalleconnect.dto.ReporteVentasMunicipioResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.DetallePedido;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import co.uniajc.agrovalle.agrovalleconnect.repositories.PedidoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private ReporteService reporteService;

    @Test
    void agrupaVentasPorMunicipioDelProductor() {
        Agricultor agricultor = new Agricultor();
        agricultor.setId(7L);
        agricultor.setMunicipio(Municipio.PALMIRA);

        LoteCosecha lote = new LoteCosecha();
        lote.setId(15L);
        lote.setAgricultor(agricultor);

        DetallePedido detalle = new DetallePedido();
        detalle.setLote(lote);
        detalle.setCantidadKg(new BigDecimal("20"));
        detalle.setSubtotal(new BigDecimal("50000"));

        Pedido pedido = new Pedido();
        pedido.setId(40L);
        pedido.setDetalles(List.of(detalle));
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));

        List<ReporteVentasMunicipioResponse> reporte =
                reporteService.ventasPorMunicipio();

        assertEquals(1, reporte.size());
        assertEquals(Municipio.PALMIRA, reporte.get(0).municipio());
        assertEquals(1L, reporte.get(0).cantidadPedidos());
        assertEquals(new BigDecimal("20.00"), reporte.get(0).cantidadKg());
        assertEquals(new BigDecimal("50000.00"), reporte.get(0).totalVentas());
    }
}
