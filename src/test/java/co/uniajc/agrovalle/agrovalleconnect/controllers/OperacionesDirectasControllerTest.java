package co.uniajc.agrovalle.agrovalleconnect.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.dto.MensajeRequest;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Mensaje;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import co.uniajc.agrovalle.agrovalleconnect.services.MensajeService;
import co.uniajc.agrovalle.agrovalleconnect.services.NotificacionService;
import co.uniajc.agrovalle.agrovalleconnect.services.PedidoService;
import java.math.BigDecimal;
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
}
