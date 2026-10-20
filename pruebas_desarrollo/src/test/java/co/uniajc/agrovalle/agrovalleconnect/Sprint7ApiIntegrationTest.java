package co.uniajc.agrovalle.agrovalleconnect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;
import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import co.uniajc.agrovalle.agrovalleconnect.models.PedidoItem;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.CarritoItemRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.ComercianteRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.MensajeRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.NotificacionRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.PedidoRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.TransaccionRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class Sprint7ApiIntegrationTest {

    private static final String PASSWORD = "clave-segura";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AgricultorRepository agricultorRepository;

    @Autowired
    private ComercianteRepository comercianteRepository;

    @Autowired
    private LoteCosechaRepository loteCosechaRepository;

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @BeforeEach
    void limpiarDatos() {
        notificacionRepository.deleteAll();
        mensajeRepository.deleteAll();
        carritoItemRepository.deleteAll();
        pedidoRepository.deleteAll();
        transaccionRepository.deleteAll();
        loteCosechaRepository.deleteAll();
        comercianteRepository.deleteAll();
        agricultorRepository.deleteAll();
    }

    @Test
    void comercianteSeRegistraPersisteYPuedeIniciarSesion() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombreCompleto": "Comerciante de prueba",
                                  "nombreNegocio": "Mercado Central",
                                  "nit": "900123456",
                                  "correo": "comercio@example.com",
                                  "contrasena": "clave-segura",
                                  "telefono": "3001234567"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.correo").value("comercio@example.com"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());

        Comerciante comerciante = comercianteRepository
                .findByCorreoIgnoreCase("comercio@example.com").orElseThrow();
        assertTrue(passwordEncoder.matches(PASSWORD, comerciante.getContrasena()));
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"correo": "comercio@example.com", "contrasena": "clave-segura"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void agricultorPuedeEditarCantidadYPrecioYDarDeBajaSuLote() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "productor@example.com");
        LoteCosecha lote = guardarLote(agricultor);
        String token = autenticarAgricultor(agricultor.getCorreo());

        mockMvc.perform(patch("/api/v1/productos/{loteId}", lote.getId())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cantidadKg": 25, "precioUnitario": 18000}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadKg").value(25))
                .andExpect(jsonPath("$.precioUnitario").value(18000))
                .andExpect(jsonPath("$.activo").value(true));

        mockMvc.perform(delete("/api/v1/productos/{loteId}", lote.getId())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));

        assertFalse(loteCosechaRepository.findById(lote.getId()).orElseThrow().getActivo());
        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void agricultorNoPuedeModificarNiDarDeBajaLoteAjeno() throws Exception {
        Agricultor propietario = guardarAgricultor("1234567890", "propietario@example.com");
        Agricultor otroAgricultor = guardarAgricultor("1234567891", "otro@example.com");
        LoteCosecha lote = guardarLote(propietario);
        String tokenAjeno = autenticarAgricultor(otroAgricultor.getCorreo());

        mockMvc.perform(patch("/api/v1/productos/{loteId}", lote.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenAjeno)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidadKg\": 25}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/v1/productos/{loteId}", lote.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenAjeno))
                .andExpect(status().isForbidden());

        LoteCosecha loteSinCambios = loteCosechaRepository.findById(lote.getId()).orElseThrow();
        assertEquals(50.0, loteSinCambios.getCantidadKg());
        assertTrue(loteSinCambios.getActivo());
    }

    @Test
    void comercianteNoPuedeEditarNiDarDeBajaLotes() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "productor@example.com");
        Comerciante comerciante = guardarComerciante();
        LoteCosecha lote = guardarLote(agricultor);
        String token = autenticarUsuario(comerciante.getCorreo());

        mockMvc.perform(patch("/api/v1/productos/{loteId}", lote.getId())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidadKg\": 25}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/v1/productos/{loteId}", lote.getId())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isForbidden());
    }

    @Test
    void loteConPedidoActivoNoSePuedeEditarNiDarDeBaja() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "productor@example.com");
        Comerciante comerciante = guardarComerciante();
        LoteCosecha lote = guardarLote(agricultor);
        Pedido pedido = guardarPedido(lote, agricultor, comerciante, EstadoPedido.RESERVADO);
        String token = autenticarAgricultor(agricultor.getCorreo());

        for (EstadoPedido estado : List.of(
                EstadoPedido.RESERVADO,
                EstadoPedido.EN_TRANSITO,
                EstadoPedido.ENTREGADO)) {
            pedido.setEstado(estado);
            pedidoRepository.save(pedido);
            mockMvc.perform(patch("/api/v1/productos/{loteId}", lote.getId())
                            .header(HttpHeaders.AUTHORIZATION, token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"cantidadKg\": 25}"))
                    .andExpect(status().isConflict());
            mockMvc.perform(delete("/api/v1/productos/{loteId}", lote.getId())
                            .header(HttpHeaders.AUTHORIZATION, token))
                    .andExpect(status().isConflict());
        }

        LoteCosecha loteSinCambios = loteCosechaRepository.findById(lote.getId()).orElseThrow();
        assertEquals(50.0, loteSinCambios.getCantidadKg());
        assertTrue(loteSinCambios.getActivo());
    }

    @Test
    void pedidoFinalizadoNoBloqueaActualizacionDelLote() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "productor@example.com");
        Comerciante comerciante = guardarComerciante();
        LoteCosecha lote = guardarLote(agricultor);
        guardarPedido(lote, agricultor, comerciante, EstadoPedido.FINALIZADO);

        mockMvc.perform(patch("/api/v1/productos/{loteId}", lote.getId())
                        .header(HttpHeaders.AUTHORIZATION,
                                autenticarAgricultor(agricultor.getCorreo()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidadKg\": 25}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadKg").value(25));
    }

    @Test
    void actualizacionParcialVaciaSeRechaza() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "productor@example.com");
        LoteCosecha lote = guardarLote(agricultor);

        mockMvc.perform(patch("/api/v1/productos/{loteId}", lote.getId())
                        .header(HttpHeaders.AUTHORIZATION,
                                autenticarAgricultor(agricultor.getCorreo()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    private Agricultor guardarAgricultor(String cedula, String correo) {
        Agricultor agricultor = new Agricultor();
        agricultor.setNombreCompleto("Productor de prueba");
        agricultor.setCedula(cedula);
        agricultor.setCorreo(correo);
        agricultor.setContrasena(passwordEncoder.encode(PASSWORD));
        agricultor.setUbicacionFinca("Finca de prueba");
        agricultor.setMunicipio(Municipio.PALMIRA);
        agricultor.setRol(Rol.PRODUCCION);
        return agricultorRepository.save(agricultor);
    }

    private Comerciante guardarComerciante() {
        Comerciante comerciante = new Comerciante();
        comerciante.setNombreCompleto("Comerciante de prueba");
        comerciante.setNombreNegocio("Mercado de prueba");
        comerciante.setNit("900123456");
        comerciante.setCorreo("comercio@example.com");
        comerciante.setContrasena(passwordEncoder.encode(PASSWORD));
        return comercianteRepository.save(comerciante);
    }

    private LoteCosecha guardarLote(Agricultor agricultor) {
        LoteCosecha lote = new LoteCosecha();
        lote.setTipoProducto("Cafe");
        lote.setCategoria("Granos");
        lote.setCantidadKg(50.0);
        lote.setPrecioUnitario(15000.0);
        lote.setFechaCosecha(LocalDate.now().plusDays(1));
        lote.setAgricultor(agricultor);
        lote.setActivo(true);
        return loteCosechaRepository.save(lote);
    }

    private Pedido guardarPedido(
            LoteCosecha lote,
            Agricultor agricultor,
            Comerciante comerciante,
            EstadoPedido estado) {
        Pedido pedido = new Pedido();
        pedido.setAgricultor(agricultor);
        pedido.setComerciante(comerciante);
        pedido.setEstado(estado);
        pedido.setTotal(15000.0);
        pedido.setFechaHora(Instant.now());

        PedidoItem item = new PedidoItem();
        item.setLote(lote);
        item.setCantidadKg(1.0);
        item.setPrecioUnitario(15000.0);
        pedido.agregarItem(item);
        return pedidoRepository.save(pedido);
    }

    private String autenticarAgricultor(String correo) throws Exception {
        return autenticarUsuario(correo);
    }

    private String autenticarUsuario(String correo) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"correo": "%s", "contrasena": "%s"}
                                """.formatted(correo, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return "Bearer " + json.path("accessToken").asString();
    }
}
