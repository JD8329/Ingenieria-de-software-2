package co.uniajc.agrovalle.agrovalleconnect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.models.DetallePedido;
import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.PedidoRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.NotificacionRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class Sprint7Hu13IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgricultorRepository agricultorRepository;

    @Autowired
    private LoteCosechaRepository loteCosechaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limpiarDatos() {
        notificacionRepository.deleteAll();
        pedidoRepository.deleteAll();
        loteCosechaRepository.deleteAll();
        agricultorRepository.deleteAll();
    }

    @Test
    void agricultorPuedeActualizarCantidadParcialmente() throws Exception {
        String token = registrarAgricultorYObtenerToken(
                "hu13-agricultor@example.com", "900001001");
        Long loteId = publicarLote(token);

        mockMvc.perform(patch("/api/v1/productos/{id}", loteId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cantidadKg": 80}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadKg").value(80))
                .andExpect(jsonPath("$.precioUnitario").value(5000));

        assertEquals(new BigDecimal("80.00"),
                loteCosechaRepository.findById(loteId).orElseThrow().getCantidadKg());
    }

    @Test
    void agricultorPuedeDarDeBajaLoteSinBorrarRegistro() throws Exception {
        String token = registrarAgricultorYObtenerToken(
                "hu13-baja@example.com", "900001002");
        Long loteId = publicarLote(token);

        mockMvc.perform(delete("/api/v1/productos/{id}", loteId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));

        assertFalse(loteCosechaRepository.findById(loteId).orElseThrow().getActivo());
    }

    @Test
    void agricultorNoPuedeModificarLoteAjeno() throws Exception {
        String propietario = registrarAgricultorYObtenerToken(
                "hu13-propietario@example.com", "900001003");
        String otroAgricultor = registrarAgricultorYObtenerToken(
                "hu13-otro@example.com", "900001004");
        Long loteId = publicarLote(propietario);

        mockMvc.perform(patch("/api/v1/productos/{id}", loteId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + otroAgricultor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"precioUnitario": 6000}
                                """))
                .andExpect(status().isForbidden());

        assertEquals(new BigDecimal("5000.00"),
                loteCosechaRepository.findById(loteId).orElseThrow().getPrecioUnitario());
    }

    @Test
    void noPermiteModificarLoteConPedidoNoFinalizado() throws Exception {
        String token = registrarAgricultorYObtenerToken(
                "hu13-pedido@example.com", "900001005");
        Long loteId = publicarLote(token);
        LoteCosecha lote = loteCosechaRepository.findById(loteId).orElseThrow();
        Pedido pedido = new Pedido();
        pedido.setComercianteId(1L);
        pedido.setEstado(EstadoPedido.RESERVADO);
        pedido.setTotal(new BigDecimal("50000.00"));
        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);
        detalle.setLote(lote);
        detalle.setCantidadKg(new BigDecimal("10.00"));
        detalle.setPrecioUnitario(new BigDecimal("5000.00"));
        detalle.setSubtotal(new BigDecimal("50000.00"));
        pedido.setDetalles(new ArrayList<>(List.of(detalle)));
        pedidoRepository.save(pedido);

        mockMvc.perform(delete("/api/v1/productos/{id}", loteId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isConflict());

        assertTrue(loteCosechaRepository.findById(loteId).orElseThrow().getActivo());
    }

    private String registrarAgricultorYObtenerToken(String correo, String cedula) throws Exception {
        mockMvc.perform(post("/api/v1/agricultores/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombreCompleto": "Agricultor Sprint 7",
                                  "cedula": "%s",
                                  "correo": "%s",
                                  "contrasena": "clave-segura",
                                  "ubicacionFinca": "Finca del Valle",
                                  "municipio": "PALMIRA"
                                }
                                """.formatted(cedula, correo)))
                .andExpect(status().isCreated());

        MvcResult resultado = mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"correo": "%s", "contrasena": "clave-segura"}
                                        """.formatted(correo)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode respuesta = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return respuesta.path("accessToken").asText();
    }

    private Long publicarLote(String token) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/api/v1/productos")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "tipoProducto": "Cafe",
                                          "categoria": "GRANOS",
                                          "cantidadKg": 100,
                                          "precioUnitario": 5000,
                                          "fechaCosecha": "2099-12-15"
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode respuesta = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return respuesta.path("id").asLong();
    }
}
