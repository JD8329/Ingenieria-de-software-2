package co.uniajc.agrovalle.agrovalleconnect;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
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
class Sprint1ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgricultorRepository agricultorRepository;

    @Autowired
    private LoteCosechaRepository loteCosechaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limpiarDatos() {
        loteCosechaRepository.deleteAll();
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
    void registrarAgricultorDuplicado_Retorna409() throws Exception {
        guardarAgricultor("1234567890", "ana@example.com");

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
                .andExpect(status().isConflict());
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
    void publicarLoteValido_Retorna201ConIdYLoPersiste() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "ana@example.com");
        LocalDate fechaCosecha = LocalDate.now().plusDays(1);
        String loteJson = """
                {
                  "tipoProducto": "Cafe",
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
                .andExpect(jsonPath("$.fechaCosecha").value(fechaCosecha.toString()));

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
                .andExpect(jsonPath("$[0].tipoProducto").value("Cafe"));

        mockMvc.perform(get("/api/v1/productos/agricultor/{id}", agricultor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", notNullValue()));
    }

    private Agricultor guardarAgricultor(String cedula, String correo) {
        Agricultor agricultor = new Agricultor();
        agricultor.setNombreCompleto("Ana Productora");
        agricultor.setCedula(cedula);
        agricultor.setCorreo(correo);
        agricultor.setContrasena(passwordEncoder.encode("clave-segura"));
        agricultor.setUbicacionFinca("Finca El Valle");
        agricultor.setMunicipio(Municipio.PALMIRA);
        agricultor.setRol(Rol.PRODUCCION);
        return agricultorRepository.save(agricultor);
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
        String token = response.path("accessToken").asText();
        assertFalse(token.isBlank());
        return "Bearer " + token;
    }

    private LoteCosecha crearLote(Agricultor agricultor, LocalDate fechaCosecha) {
        LoteCosecha lote = new LoteCosecha();
        lote.setTipoProducto("Cafe");
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
                  "cantidadKg": 500,
                  "precioUnitario": 15000,
                  "fechaCosecha": "%s"
                }
                """.formatted(fechaCosecha);
    }
}
