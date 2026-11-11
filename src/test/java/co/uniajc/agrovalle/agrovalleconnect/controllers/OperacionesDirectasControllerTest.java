package co.uniajc.agrovalle.agrovalleconnect.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.dto.MensajeRequest;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.DetallePedido;
import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Mensaje;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import co.uniajc.agrovalle.agrovalleconnect.services.MensajeService;
import co.uniajc.agrovalle.agrovalleconnect.services.NotificacionService;
import co.uniajc.agrovalle.agrovalleconnect.services.PedidoService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        MensajeController.class,
        NotificacionController.class,
        PedidoController.class
})
class OperacionesDirectasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MensajeService mensajeService;

    @MockitoBean
    private NotificacionService notificacionService;

    @MockitoBean
    private PedidoService pedidoService;

    @Test
    void crearMensajeValidoRetorna201YDatosDelLote() throws Exception {
        Agricultor agricultor = new Agricultor();
        agricultor.setId(7L);
        LoteCosecha lote = new LoteCosecha();
        lote.setId(15L);
        lote.setAgricultor(agricultor);
        Mensaje mensaje = new Mensaje();
        mensaje.setId(21L);
        mensaje.setLote(lote);
        mensaje.setComercianteId(3L);
        mensaje.setContenido("Consulta de disponibilidad");
        when(mensajeService.enviar(any(MensajeRequest.class))).thenReturn(mensaje);

        mockMvc.perform(post("/api/v1/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "comercianteId": 3,
                                  "loteId": 15,
                                  "contenido": "Consulta de disponibilidad"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.loteId").value(15))
                .andExpect(jsonPath("$.agricultorId").value(7));
    }

    @Test
    void crearPedidoValidoRetorna201ConEstadoReservado() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(40L);
        pedido.setComercianteId(3L);
        pedido.setEstado(EstadoPedido.RESERVADO);
        pedido.setTotal(new BigDecimal("50000.00"));
        when(pedidoService.reservar(any())).thenReturn(pedido);

        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "comercianteId": 3,
                                  "items": [
                                    { "loteId": 15, "cantidadKg": 20 }
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("RESERVADO"))
                .andExpect(jsonPath("$.total").value(50000.00));
    }

    @Test
    void pedidoSinItemsRetorna400SinInvocarServicio() throws Exception {
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "comercianteId": 3,
                                  "items": []
                                }
                                """))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(pedidoService);
    }

    @Test
    void programarDespachoValidoRetorna200ConEstadoEnTransito() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(40L);
        pedido.setComercianteId(3L);
        pedido.setEstado(EstadoPedido.EN_TRANSITO);
        pedido.setTransportadora("Transporte Valle Verde");
        pedido.setConductor("Luis Perez");
        pedido.setVehiculo("XYZ-123");
        pedido.setFechaEstimadaEntrega(LocalDateTime.now().plusDays(2));
        pedido.setRuta("Palmira -> Cali");
        when(pedidoService.programarDespacho(eq(40L), any(), any())).thenReturn(pedido);

        mockMvc.perform(post("/api/v1/pedidos/40/despacho")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "transportadora": "Transporte Valle Verde",
                                  "conductor": "Luis Perez",
                                  "vehiculo": "XYZ-123",
                                  "fechaEstimadaEntrega": "2099-12-15T09:00:00",
                                  "ruta": ["Palmira", "Cali"],
                                  "observaciones": "Salida programada"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedidoId").value(40))
                .andExpect(jsonPath("$.estado").value("EN_TRANSITO"));
    }

    @Test
    void calificarPedidoValidoRetorna200ConEstadoFinalizado() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(40L);
        pedido.setComercianteId(3L);
        pedido.setEstado(EstadoPedido.FINALIZADO);
        pedido.setCalificacion(5);
        pedido.setComentarioCalificacion("Excelente entrega");
        Agricultor agricultor = new Agricultor();
        agricultor.setId(7L);
        LoteCosecha lote = new LoteCosecha();
        lote.setAgricultor(agricultor);
        DetallePedido detalle = new DetallePedido();
        detalle.setLote(lote);
        pedido.setDetalles(List.of(detalle));
        when(pedidoService.calificarPedido(eq(40L), any())).thenReturn(pedido);
        when(pedidoService.obtenerPromedioCalificacionAgricultor(7L)).thenReturn(new BigDecimal("5.00"));

        mockMvc.perform(post("/api/v1/pedidos/40/calificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "comercianteId": 3,
                                  "calificacion": 5,
                                  "comentario": "Excelente entrega"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedidoId").value(40))
                .andExpect(jsonPath("$.estado").value("FINALIZADO"));
    }
}
