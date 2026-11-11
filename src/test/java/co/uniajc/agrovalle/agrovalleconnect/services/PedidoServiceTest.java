package co.uniajc.agrovalle.agrovalleconnect.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.uniajc.agrovalle.agrovalleconnect.dto.PedidoItemRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.PedidoRequest;
import co.uniajc.agrovalle.agrovalleconnect.exceptions.ConflictoException;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Notificacion;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.NotificacionRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.PedidoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private LoteCosechaRepository loteCosechaRepository;

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void reservarPedidoDescuentaInventarioYNotificaAlAgricultor() {
        LoteCosecha lote = lote("100", "2500", "1");
        when(loteCosechaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(lote));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocacion -> {
            Pedido pedido = invocacion.getArgument(0);
            pedido.setId(40L);
            return pedido;
        });

        Pedido pedido = pedidoService.reservar(new PedidoRequest(3L,
                List.of(new PedidoItemRequest(1L, new BigDecimal("20")))));

        assertEquals(EstadoPedido.RESERVADO, pedido.getEstado());
        assertEquals(new BigDecimal("80"), lote.getCantidadKg());
        assertEquals(new BigDecimal("50000.00"), pedido.getTotal());
        assertEquals(1, pedido.getDetalles().size());
        verify(notificacionRepository).saveAll(anyList());
    }

    @Test
    void reservarPedidoConExistenciasInsuficientesNoCreaPedidoNiNotificacion() {
        LoteCosecha lote = lote("2", "2500", "1");
        when(loteCosechaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(lote));

        assertThrows(ConflictoException.class, () -> pedidoService.reservar(new PedidoRequest(3L,
                List.of(new PedidoItemRequest(1L, new BigDecimal("3"))))));

        assertEquals(new BigDecimal("2"), lote.getCantidadKg());
        verify(pedidoRepository, never()).save(any(Pedido.class));
        verify(notificacionRepository, never()).saveAll(anyList());
    }

    @Test
    void reservarPedidoConLoteRepetidoEsRechazado() {
        PedidoRequest solicitud = new PedidoRequest(3L, List.of(
                new PedidoItemRequest(1L, BigDecimal.ONE),
                new PedidoItemRequest(1L, BigDecimal.ONE)));

        assertThrows(IllegalArgumentException.class, () -> pedidoService.reservar(solicitud));
        verify(loteCosechaRepository, never()).findByIdForUpdate(any());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    private LoteCosecha lote(String cantidad, String precio, String id) {
        Agricultor agricultor = new Agricultor();
        agricultor.setId(7L);
        LoteCosecha lote = new LoteCosecha();
        lote.setId(Long.valueOf(id));
        lote.setCantidadKg(new BigDecimal(cantidad));
        lote.setPrecioUnitario(new BigDecimal(precio));
        lote.setActivo(true);
        lote.setAgricultor(agricultor);
        return lote;
    }
}
