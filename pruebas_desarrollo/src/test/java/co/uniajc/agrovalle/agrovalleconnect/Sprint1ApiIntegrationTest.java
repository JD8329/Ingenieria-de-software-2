package co.uniajc.agrovalle.agrovalleconnect;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.models.Transaccion;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class Sprint1ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgricultorRepository agricultorRepository;

    @Autowired
    private LoteCosechaRepository loteCosechaRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ComercianteRepository comercianteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

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
    void registrarAgricultorValido_Retorna201YNoExponeContrasena() throws Exception {
        mockMvc.perform(post("/api/v1/agricultores/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombreCompleto": "Ana Productora",
                                  "cedula": "1234567890",
                                  "correo": "ana@example.com",
                                  "contrasena": "clave-segura",
                                  "ubicacionFinca": "Finca El Valle",
                                  "municipio": "PALMIRA"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.municipio").value("PALMIRA"))
                .andExpect(jsonPath("$.rol").value("PRODUCCION"));

        Agricultor agricultor = agricultorRepository.findByCorreo("ana@example.com").orElseThrow();
        assertNotEquals("clave-segura", agricultor.getContrasena());
        assertTrue(passwordEncoder.matches("clave-segura", agricultor.getContrasena()));
    }

    @Test
    void registrarAgricultorConCorreoDuplicado_Retorna409() throws Exception {
        guardarAgricultor("1234567890", "ana@example.com");

        mockMvc.perform(post("/api/v1/agricultores/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson("0987654321", "ana@example.com",
                                "clave-segura", "PALMIRA")))
                .andExpect(status().isConflict());

        assertEquals(1, agricultorRepository.count());
    }

    @Test
    void registrarAgricultorConCedulaDuplicada_Retorna409() throws Exception {
        guardarAgricultor("1234567890", "ana@example.com");

        mockMvc.perform(post("/api/v1/agricultores/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson("1234567890", "otra@example.com",
                                "clave-segura", "PALMIRA")))
                .andExpect(status().isConflict());

        assertEquals(1, agricultorRepository.count());
    }

    @Test
    void persistenciaRechazaCorreoDuplicadoAunqueSeOmitaValidacionDelServicio() {
        guardarAgricultor("1234567890", "ana@example.com");

        assertThrows(DataIntegrityViolationException.class,
                () -> agricultorRepository.saveAndFlush(crearAgricultor(
                        "0987654321", "ana@example.com")));
    }

    @Test
    void persistenciaRechazaCedulaDuplicadaAunqueSeOmitaValidacionDelServicio() {
        guardarAgricultor("1234567890", "ana@example.com");

        assertThrows(DataIntegrityViolationException.class,
                () -> agricultorRepository.saveAndFlush(crearAgricultor(
                        "1234567890", "otra@example.com")));
    }

    @Test
    void registrarAgricultorSinCamposObligatorios_Retorna400() throws Exception {
        mockMvc.perform(post("/api/v1/agricultores/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles").exists());
    }

    @Test
    void registrarAgricultorConCorreoInvalido_Retorna400() throws Exception {
        mockMvc.perform(post("/api/v1/agricultores/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson("1234567890", "correo-invalido",
                                "clave-segura", "PALMIRA")))
                .andExpect(status().isBadRequest());

        assertEquals(0, agricultorRepository.count());
    }

    @Test
    void registrarAgricultorConCedulaFueraDeRango_Retorna400() throws Exception {
        mockMvc.perform(post("/api/v1/agricultores/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson("12345", "ana@example.com",
                                "clave-segura", "PALMIRA")))
                .andExpect(status().isBadRequest());

        assertEquals(0, agricultorRepository.count());
    }

    @Test
    void registrarAgricultorConContrasenaCorta_Retorna400() throws Exception {
        mockMvc.perform(post("/api/v1/agricultores/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson("1234567890", "ana@example.com",
                                "corta", "PALMIRA")))
                .andExpect(status().isBadRequest());

        assertEquals(0, agricultorRepository.count());
    }

    @Test
    void registrarAgricultorConMunicipioInvalido_Retorna400() throws Exception {
        mockMvc.perform(post("/api/v1/agricultores/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson("1234567890", "ana@example.com",
                                "clave-segura", "MUNICIPIO_INEXISTENTE")))
                .andExpect(status().isBadRequest());

        assertEquals(0, agricultorRepository.count());
    }

    @Test
    void publicarLoteValido_Retorna201ConIdYLoPersiste() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "ana@example.com");
        LocalDate fechaCosecha = LocalDate.now().plusDays(1);
        String loteJson = """
                {
                  "tipoProducto": "Cafe",
                  "categoria": "Granos",
                  "cantidadKg": 500,
                  "precioUnitario": 15000,
                  "fechaCosecha": "%s"
                }
                """.formatted(fechaCosecha);

        mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(agricultor))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loteJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.fechaCosecha").value(fechaCosecha.toString()))
                .andExpect(jsonPath("$.agricultorId").value(agricultor.getId()))
                .andExpect(jsonPath("$.agricultor").doesNotExist())
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.correo").doesNotExist())
                .andExpect(jsonPath("$.cedula").doesNotExist());

        assertEquals(1, loteCosechaRepository.count());
    }

    @Test
    void publicarLoteConFechaPasada_Retorna400() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "ana@example.com");

        mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(agricultor))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loteJson(LocalDate.now().minusDays(1))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void publicarLoteConCantidadCero_Retorna400() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "ana@example.com");
        String loteJson = """
                {
                  "tipoProducto": "Cafe",
                  "cantidadKg": 0,
                  "precioUnitario": 15000,
                  "fechaCosecha": "%s"
                }
                """.formatted(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(agricultor))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loteJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void publicarLoteParaAgricultorInexistente_Retorna400() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "ana@example.com");
        String token = bearerToken(agricultor);
        agricultorRepository.delete(agricultor);

        mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loteJson(LocalDate.now().plusDays(1))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void publicarLoteSinToken_Retorna401() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loteJson(LocalDate.now().plusDays(1))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publicarLoteConTokenInvalido_Retorna401() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token.invalido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loteJson(LocalDate.now().plusDays(1))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginConCredencialesInvalidas_Retorna401() throws Exception {
        guardarAgricultor("1234567890", "ana@example.com");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "correo": "ana@example.com",
                                  "contrasena": "incorrecta"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarLotesActivos_RetornaLotesPersistidos() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "ana@example.com");
        LoteCosecha lote = crearLote(agricultor, LocalDate.now().plusDays(1));
        loteCosechaRepository.save(lote);

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoProducto").value("Cafe"))
                .andExpect(jsonPath("$[0].agricultor").doesNotExist())
                .andExpect(jsonPath("$[0].contrasena").doesNotExist())
                .andExpect(jsonPath("$[0].correo").doesNotExist())
                .andExpect(jsonPath("$[0].cedula").doesNotExist());

        mockMvc.perform(get("/api/v1/productos/agricultor/{id}", agricultor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].agricultor").doesNotExist())
                .andExpect(jsonPath("$[0].contrasena").doesNotExist())
                .andExpect(jsonPath("$[0].correo").doesNotExist())
                .andExpect(jsonPath("$[0].cedula").doesNotExist());
    }

    @Test
    void buscarLotesPorMunicipioYCategoria_RetornaSoloCoincidenciasActivas() throws Exception {
        Agricultor agricultorDagua = agricultorRepository.save(
                crearAgricultor("1234567890", "dagua@example.com"));
        agricultorDagua.setMunicipio(Municipio.DAGUA);
        agricultorDagua = agricultorRepository.save(agricultorDagua);
        Agricultor agricultorPalmira = guardarAgricultor("0987654321", "palmira@example.com");

        LoteCosecha loteCoincidente = crearLote(agricultorDagua, LocalDate.now().plusDays(1));
        loteCoincidente.setTipoProducto("Mango");
        loteCoincidente.setCategoria("Frutas");
        loteCosechaRepository.save(loteCoincidente);

        LoteCosecha loteOtraCategoria = crearLote(agricultorDagua, LocalDate.now().plusDays(1));
        loteOtraCategoria.setCategoria("Granos");
        loteCosechaRepository.save(loteOtraCategoria);

        LoteCosecha loteOtroMunicipio = crearLote(agricultorPalmira, LocalDate.now().plusDays(1));
        loteOtroMunicipio.setCategoria("Frutas");
        loteCosechaRepository.save(loteOtroMunicipio);

        mockMvc.perform(get("/api/v1/productos/buscar")
                        .param("municipio", "Dagua")
                        .param("categoria", "frutas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(loteCoincidente.getId()))
                .andExpect(jsonPath("$[0].categoria").value("Frutas"))
                .andExpect(jsonPath("$[0].cantidadKg").value(500.0))
                .andExpect(jsonPath("$[0].precioUnitario").value(15000.0));
    }

    @Test
    void buscarLotesConMunicipioInvalido_Retorna400() throws Exception {
        mockMvc.perform(get("/api/v1/productos/buscar").param("municipio", "MunicipioInventado"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrarVentaAutenticada_ReduceStockYGuardaTransaccion() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "ana@example.com");
        LoteCosecha lote = loteCosechaRepository.save(
                crearLote(agricultor, LocalDate.now().plusDays(1)));

        mockMvc.perform(post("/api/v1/productos/{id}/transacciones", lote.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(agricultor))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cantidadKg": 20, "precioUnitario": 17000}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.loteId").value(lote.getId()))
                .andExpect(jsonPath("$.cantidadKg").value(20.0))
                .andExpect(jsonPath("$.fechaHora").exists());

        assertEquals(1, transaccionRepository.count());
        assertEquals(480.0, loteCosechaRepository.findById(lote.getId()).orElseThrow().getCantidadKg());
    }

    @Test
    void registrarVentaDeLoteAjeno_Retorna403() throws Exception {
        Agricultor propietario = guardarAgricultor("1234567890", "propietario@example.com");
        Agricultor otroAgricultor = guardarAgricultor("0987654321", "otro@example.com");
        LoteCosecha lote = loteCosechaRepository.save(
                crearLote(propietario, LocalDate.now().plusDays(1)));

        mockMvc.perform(post("/api/v1/productos/{id}/transacciones", lote.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(otroAgricultor))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cantidadKg": 20, "precioUnitario": 17000}
                                """))
                .andExpect(status().isForbidden());

        assertEquals(0, transaccionRepository.count());
    }

    @Test
    void registrarVentaSinToken_Retorna401() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "ana@example.com");
        LoteCosecha lote = loteCosechaRepository.save(
                crearLote(agricultor, LocalDate.now().plusDays(1)));

        mockMvc.perform(post("/api/v1/productos/{id}/transacciones", lote.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cantidadKg": 20, "precioUnitario": 17000}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registrarVentaSuperiorAlStock_Retorna409YSinCambios() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "ana@example.com");
        LoteCosecha lote = loteCosechaRepository.save(
                crearLote(agricultor, LocalDate.now().plusDays(1)));

        mockMvc.perform(post("/api/v1/productos/{id}/transacciones", lote.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(agricultor))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cantidadKg": 501, "precioUnitario": 17000}
                                """))
                .andExpect(status().isConflict());

        assertEquals(0, transaccionRepository.count());
        assertEquals(500.0, loteCosechaRepository.findById(lote.getId()).orElseThrow().getCantidadKg());
    }

    @Test
    void precioPromedioSoloUsaTransaccionesDeLasUltimas24HorasYExigeCincuenta() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "ana@example.com");
        LoteCosecha lote = loteCosechaRepository.save(
                crearLote(agricultor, LocalDate.now().plusDays(1)));
        Instant ahora = Instant.now();
        for (int indice = 0; indice < 49; indice++) {
            transaccionRepository.save(crearTransaccion(
                    lote, indice % 2 == 0 ? 10000.0 : 20000.0, ahora));
        }
        transaccionRepository.save(crearTransaccion(lote, 900000.0, ahora.minusSeconds(86401)));

        mockMvc.perform(get("/api/v1/precios/promedio").param("producto", "cafe"))
                .andExpect(status().is(422));

        transaccionRepository.save(crearTransaccion(lote, 10000.0, ahora));
        mockMvc.perform(get("/api/v1/precios/promedio").param("producto", "CAFE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadTransacciones").value(50))
                .andExpect(jsonPath("$.precioPromedio").value(14800.0))
                .andExpect(jsonPath("$.moneda").value("COP"));
    }

    private Agricultor guardarAgricultor(String cedula, String correo) {
        return agricultorRepository.save(crearAgricultor(cedula, correo));
    }

    private Agricultor crearAgricultor(String cedula, String correo) {
        Agricultor agricultor = new Agricultor();
        agricultor.setNombreCompleto("Ana Productora");
        agricultor.setCedula(cedula);
        agricultor.setCorreo(correo);
        agricultor.setContrasena(passwordEncoder.encode("clave-segura"));
        agricultor.setUbicacionFinca("Finca El Valle");
        agricultor.setMunicipio(Municipio.PALMIRA);
        agricultor.setRol(Rol.PRODUCCION);
        return agricultor;
    }

    private String registroJson(String cedula, String correo, String contrasena, String municipio) {
        return """
                {
                  "nombreCompleto": "Ana Productora",
                  "cedula": "%s",
                  "correo": "%s",
                  "contrasena": "%s",
                  "ubicacionFinca": "Finca El Valle",
                  "municipio": "%s"
                }
                """.formatted(cedula, correo, contrasena, municipio);
    }

    private String bearerToken(Agricultor agricultor) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "correo": "%s",
                                  "contrasena": "clave-segura"
                                }
                                """.formatted(agricultor.getCorreo())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        String token = response.path("accessToken").asString();
        assertFalse(token.isBlank());
        return "Bearer " + token;
    }

    private LoteCosecha crearLote(Agricultor agricultor, LocalDate fechaCosecha) {
        LoteCosecha lote = new LoteCosecha();
        lote.setTipoProducto("Cafe");
        lote.setCategoria("Granos");
        lote.setCantidadKg(500.0);
        lote.setPrecioUnitario(15000.0);
        lote.setFechaCosecha(fechaCosecha);
        lote.setAgricultor(agricultor);
        lote.setActivo(true);
        return lote;
    }

    private String loteJson(LocalDate fechaCosecha) {
        return """
                {
                  "tipoProducto": "Cafe",
                  "categoria": "Granos",
                  "cantidadKg": 500,
                  "precioUnitario": 15000,
                  "fechaCosecha": "%s"
                }
                """.formatted(fechaCosecha);
    }

    private Transaccion crearTransaccion(LoteCosecha lote, Double precio, Instant fechaHora) {
        Transaccion transaccion = new Transaccion();
        transaccion.setLote(lote);
        transaccion.setCantidadKg(1.0);
        transaccion.setPrecioUnitario(precio);
        transaccion.setFechaHora(fechaHora);
        return transaccion;
    }
}
