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
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.models.TipoNotificacion;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.CarritoItemRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.ComercianteRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.MensajeRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.NotificacionRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.PedidoRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.TransaccionRepository;
import java.time.LocalDate;
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
class Sprint3ApiIntegrationTest {

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
    void checkoutCreaUnPedidoPorAgricultorYReservaInventario() throws Exception {
        Agricultor primerAgricultor = guardarAgricultor("1234567890", "primero@example.com");
        Agricultor segundoAgricultor = guardarAgricultor("1234567891", "segundo@example.com");
        LoteCosecha primerLote = guardarLote(primerAgricultor, 10.0);
        LoteCosecha segundoLote = guardarLote(segundoAgricultor, 20.0);
        String token = registrarYAutenticarComerciante();

        agregarAlCarrito(token, primerLote.getId(), 3.0);
        agregarAlCarrito(token, segundoLote.getId(), 4.0);
        mockMvc.perform(get("/api/v1/carrito")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(post("/api/v1/pedidos")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].estado").value("RESERVADO"))
                .andExpect(jsonPath("$[0].items.length()").value(1))
                .andExpect(jsonPath("$[1].estado").value("RESERVADO"));

        assertEquals(2, pedidoRepository.count());
        assertEquals(4, notificacionRepository.count());
        Long comercianteId = comercianteRepository
                .findByCorreoIgnoreCase("comercio@example.com").orElseThrow().getId();
        assertEquals(2, notificacionRepository
                .findByDestinatarioIdAndDestinatarioRolOrderByFechaHoraDesc(
                        comercianteId, Rol.COMERCIANTE).size());
        assertEquals(1, notificacionRepository
                .findByDestinatarioIdAndDestinatarioRolOrderByFechaHoraDesc(
                        primerAgricultor.getId(), Rol.PRODUCCION).size());
        assertEquals(1, notificacionRepository
                .findByDestinatarioIdAndDestinatarioRolOrderByFechaHoraDesc(
                        segundoAgricultor.getId(), Rol.PRODUCCION).size());
        assertTrue(notificacionRepository.findAll().stream()
                .allMatch(notificacion ->
                        notificacion.getTipo() == TipoNotificacion.PEDIDO_ESTADO_CAMBIADO));
        assertEquals(7.0, loteCosechaRepository.findById(primerLote.getId()).orElseThrow().getCantidadKg());
        assertEquals(16.0, loteCosechaRepository.findById(segundoLote.getId()).orElseThrow().getCantidadKg());
        assertTrue(carritoItemRepository.findByComercianteIdOrderByLoteId(
                comercianteRepository.findByCorreoIgnoreCase("comercio@example.com")
                        .orElseThrow().getId()).isEmpty());
    }

    @Test
    void mensajesSePersistenYGeneranNotificacionPrivadaAlAgricultor() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "productor@example.com");
        Agricultor otroAgricultor = guardarAgricultor("1234567891", "otro@example.com");
        LoteCosecha lote = guardarLote(agricultor, 10.0);
        String tokenComerciante = registrarYAutenticarComerciante();
        String tokenAgricultor = autenticarAgricultor(agricultor.getCorreo());
        String tokenOtroAgricultor = autenticarAgricultor(otroAgricultor.getCorreo());

        mockMvc.perform(post("/api/v1/mensajes")
                        .header(HttpHeaders.AUTHORIZATION, tokenComerciante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"loteId": %d, "contenido": "¿Cuál es la fecha de entrega?"}
                                """.formatted(lote.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contenido").value("¿Cuál es la fecha de entrega?"))
                .andExpect(jsonPath("$.loteId").value(lote.getId()));

        mockMvc.perform(get("/api/v1/mensajes")
                        .header(HttpHeaders.AUTHORIZATION, tokenAgricultor)
                        .param("loteId", lote.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(get("/api/v1/mensajes")
                        .header(HttpHeaders.AUTHORIZATION, tokenOtroAgricultor)
                        .param("loteId", lote.getId().toString()))
                .andExpect(status().isForbidden());
        assertEquals(1, mensajeRepository.count());
        assertEquals(1, notificacionRepository.count());

        MvcResult notificaciones = mockMvc.perform(get("/api/v1/notificaciones")
                        .header(HttpHeaders.AUTHORIZATION, tokenAgricultor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].leida").value(false))
                .andReturn();
        JsonNode json = objectMapper.readTree(notificaciones.getResponse().getContentAsString());
        long id = json.get(0).path("id").asLong();
        mockMvc.perform(patch("/api/v1/notificaciones/{id}/leer", id)
                        .header(HttpHeaders.AUTHORIZATION, tokenAgricultor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leida").value(true));
        assertTrue(notificacionRepository.findById(id).orElseThrow().isLeida());
    }

    @Test
    void comercianteSoloConsultaSuPropiaConversacion() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "productor@example.com");
        LoteCosecha lote = guardarLote(agricultor, 10.0);
        String primerComerciante = registrarYAutenticarComerciante();
        String segundoComerciante = registrarYAutenticarComerciante(
                "900123457", "otro-comercio@example.com", "Otro Mercado");

        enviarMensaje(primerComerciante, lote.getId(), "Mensaje privado uno");
        enviarMensaje(segundoComerciante, lote.getId(), "Mensaje privado dos");

        mockMvc.perform(get("/api/v1/mensajes")
                        .header(HttpHeaders.AUTHORIZATION, primerComerciante)
                        .param("loteId", lote.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].contenido").value("Mensaje privado uno"));
    }

    @Test
    void checkoutConStockInsuficienteNoReservaNiVacíaElCarrito() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "productor@example.com");
        LoteCosecha lote = guardarLote(agricultor, 10.0);
        String token = registrarYAutenticarComerciante();
        agregarAlCarrito(token, lote.getId(), 8.0);
        lote.setCantidadKg(5.0);
        loteCosechaRepository.save(lote);

        mockMvc.perform(post("/api/v1/pedidos")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isConflict());

        assertEquals(5.0, loteCosechaRepository.findById(lote.getId()).orElseThrow().getCantidadKg());
        assertEquals(1, carritoItemRepository.count());
        assertEquals(0, pedidoRepository.count());
        assertEquals(0, notificacionRepository.count());
    }

    @Test
    void endpointsDeComercianteRechazanTokenDeAgricultor() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "productor@example.com");
        String token = autenticarAgricultor(agricultor.getCorreo());

        mockMvc.perform(get("/api/v1/carrito")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/pedidos")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/mensajes")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loteId\": 1, \"contenido\": \"Mensaje\"}"))
                .andExpect(status().isForbidden());

        String tokenComerciante = registrarYAutenticarComerciante();
        mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, tokenComerciante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tipoProducto": "Cafe",
                                  "categoria": "Granos",
                                  "cantidadKg": 10,
                                  "precioUnitario": 1000,
                                  "fechaCosecha": "%s"
                                }
                                """.formatted(LocalDate.now().plusDays(1))))
                .andExpect(status().isForbidden());
    }

    @Test
    void carritoRechazaCantidadMayorAlStockYPermiteRetirarLote() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "productor@example.com");
        LoteCosecha lote = guardarLote(agricultor, 5.0);
        String token = registrarYAutenticarComerciante();

        mockMvc.perform(post("/api/v1/carrito/items")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"loteId": %d, "cantidadKg": 6}
                                """.formatted(lote.getId())))
                .andExpect(status().isConflict());
        agregarAlCarrito(token, lote.getId(), 2.0);
        mockMvc.perform(delete("/api/v1/carrito/items/{loteId}", lote.getId())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent());
        assertEquals(0, carritoItemRepository.count());
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

    private LoteCosecha guardarLote(Agricultor agricultor, double cantidad) {
        LoteCosecha lote = new LoteCosecha();
        lote.setTipoProducto("Cafe");
        lote.setCategoria("Granos");
        lote.setCantidadKg(cantidad);
        lote.setPrecioUnitario(15000.0);
        lote.setFechaCosecha(LocalDate.now().plusDays(1));
        lote.setAgricultor(agricultor);
        lote.setActivo(true);
        return loteCosechaRepository.save(lote);
    }

    private String registrarYAutenticarComerciante() throws Exception {
        return registrarYAutenticarComerciante(
                "900123456", "comercio@example.com", "Mercado Central");
    }

    private String registrarYAutenticarComerciante(
            String nit,
            String correo,
            String nombreNegocio) throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombreCompleto": "Comerciante de prueba",
                                  "nombreNegocio": "%s",
                                  "nit": "%s",
                                  "correo": "%s",
                                  "contrasena": "clave-segura",
                                  "telefono": "3001234567"
                                }
                                """.formatted(nombreNegocio, nit, correo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contrasena").doesNotExist());
        Comerciante comerciante = comercianteRepository.findByCorreoIgnoreCase(correo).orElseThrow();
        assertFalse(passwordEncoder.matches("texto-incorrecto", comerciante.getContrasena()));
        return autenticar(correo);
    }

    private String autenticarAgricultor(String correo) throws Exception {
        return autenticar(correo);
    }

    private String autenticar(String correo) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"correo": "%s", "contrasena": "%s"}
                                """.formatted(correo, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();
        return "Bearer " + objectMapper.readTree(result.getResponse().getContentAsString())
                .path("accessToken").asString();
    }

    private void agregarAlCarrito(String token, Long loteId, double cantidad) throws Exception {
        mockMvc.perform(post("/api/v1/carrito/items")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"loteId": %d, "cantidadKg": %s}
                                """.formatted(loteId, cantidad)))
                .andExpect(status().isCreated());
    }

    private void enviarMensaje(String token, Long loteId, String contenido) throws Exception {
        mockMvc.perform(post("/api/v1/mensajes")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"loteId": %d, "contenido": "%s"}
                                """.formatted(loteId, contenido)))
                .andExpect(status().isCreated());
    }
}
