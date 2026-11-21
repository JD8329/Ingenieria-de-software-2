package co.uniajc.agrovalle.agrovalleconnect;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.DetallePedido;
import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.NotificacionRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.PedidoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class Sprint7Hu14IntegrationTest {

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

    @BeforeEach
    void limpiarDatos() {
        notificacionRepository.deleteAll();
        pedidoRepository.deleteAll();
        loteCosechaRepository.deleteAll();
        agricultorRepository.deleteAll();
    }

    @Test
    void perfilPublicoIncluyeCalificacionCatalogoActivoYSoloDatosPublicos() throws Exception {
        Agricultor agricultor = registrarAgricultor(
                "hu14-agricultor@example.com", "900002001");
        LoteCosecha cafe = crearLote(agricultor, "Cafe", true);
        LoteCosecha mango = crearLote(agricultor, "Mango", true);
        LoteCosecha cacaoInactivo = crearLote(agricultor, "Cacao", false);
        guardarPedidoCalificado(agricultor, 5, cafe, mango);
        guardarPedidoCalificado(agricultor, 3, cacaoInactivo);

        mockMvc.perform(get("/api/v1/agricultores/{id}/perfil", agricultor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agricultorId").value(agricultor.getId()))
                .andExpect(jsonPath("$.nombreCompleto").value("Agricultor Sprint 7"))
                .andExpect(jsonPath("$.nombreFinca").value("Finca del Valle"))
                .andExpect(jsonPath("$.municipio").value("PALMIRA"))
                .andExpect(jsonPath("$.calificacionPromedio").value(4.0))
                .andExpect(jsonPath("$.totalCalificaciones").value(2))
                .andExpect(jsonPath("$.lotesActivos").value(2))
                .andExpect(jsonPath("$.catalogoActivo.length()").value(2))
                .andExpect(jsonPath("$.catalogoActivo[*].tipoProducto")
                        .value(containsInAnyOrder("Cafe", "Mango")))
                .andExpect(jsonPath("$.catalogoActivo[0].activo").value(true))
                .andExpect(jsonPath("$.catalogoActivo[1].activo").value(true))
                .andExpect(jsonPath("$.correo").doesNotExist())
                .andExpect(jsonPath("$.cedula").doesNotExist())
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.telefono").doesNotExist());
    }

    @Test
    void perfilSinLotesNiCalificacionesRetornaColeccionesVacias() throws Exception {
        Agricultor agricultor = registrarAgricultor(
                "hu14-sin-datos@example.com", "900002002");

        mockMvc.perform(get("/api/v1/agricultores/{id}/perfil", agricultor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calificacionPromedio").value(0.0))
                .andExpect(jsonPath("$.totalCalificaciones").value(0))
                .andExpect(jsonPath("$.lotesActivos").value(0))
                .andExpect(jsonPath("$.catalogoActivo").isEmpty());
    }

    @Test
    void agricultorInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/api/v1/agricultores/999999/perfil"))
                .andExpect(status().isNotFound());
    }

    private Agricultor registrarAgricultor(String correo, String cedula) throws Exception {
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
        return agricultorRepository.findByCorreo(correo).orElseThrow();
    }

    private LoteCosecha crearLote(Agricultor agricultor, String producto, boolean activo) {
        LoteCosecha lote = new LoteCosecha();
        lote.setTipoProducto(producto);
        lote.setCategoria("FRUTAS");
        lote.setCantidadKg(new BigDecimal("100.00"));
        lote.setPrecioUnitario(new BigDecimal("5000.00"));
        lote.setFechaCosecha(LocalDate.now().plusDays(1));
        lote.setAgricultor(agricultor);
        lote.setActivo(activo);
        return loteCosechaRepository.save(lote);
    }

    private void guardarPedidoCalificado(Agricultor agricultor, int calificacion, LoteCosecha... lotes) {
        Pedido pedido = new Pedido();
        pedido.setComercianteId(1L);
        pedido.setEstado(EstadoPedido.FINALIZADO);
        pedido.setCalificacion(calificacion);
        pedido.setTotal(new BigDecimal("50000.00"));
        pedido.setDetalles(new ArrayList<>());
        for (LoteCosecha lote : lotes) {
            if (!agricultor.getId().equals(lote.getAgricultor().getId())) {
                throw new IllegalArgumentException("El lote debe pertenecer al agricultor de la prueba");
            }
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setLote(lote);
            detalle.setCantidadKg(new BigDecimal("10.00"));
            detalle.setPrecioUnitario(new BigDecimal("5000.00"));
            detalle.setSubtotal(new BigDecimal("50000.00"));
            pedido.getDetalles().add(detalle);
        }
        pedidoRepository.save(pedido);
    }
}
