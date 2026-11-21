package co.uniajc.agrovalle.agrovalleconnect;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.ComercianteRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class Sprint6ComercianteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgricultorRepository agricultorRepository;

    @Autowired
    private ComercianteRepository comercianteRepository;

    @Autowired
    private LoteCosechaRepository loteCosechaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limpiarDatos() {
        loteCosechaRepository.deleteAll();
        comercianteRepository.deleteAll();
        agricultorRepository.deleteAll();
    }

    @Test
    void registrarComercianteValido_Retorna201SinExponerContrasena() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "razonSocial": "Mercado del Valle S.A.S.",
                                  "nit": "900123456-7",
                                  "correo": "compras@mercado.example",
                                  "contrasena": "clave-segura",
                                  "telefono": "3001234567",
                                  "direccion": "Calle 10 # 20-30",
                                  "municipio": "CALI"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.razonSocial").value("Mercado del Valle S.A.S."))
                .andExpect(jsonPath("$.nit").value("900123456-7"))
                .andExpect(jsonPath("$.correo").value("compras@mercado.example"))
                .andExpect(jsonPath("$.municipio").value("CALI"))
                .andExpect(jsonPath("$.rol").value("COMERCIANTE"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());

        Comerciante comerciante = comercianteRepository
                .findByCorreo("compras@mercado.example").orElseThrow();
        assertEquals("900123456-7", comerciante.getNit());
        assertEquals(Municipio.CALI, comerciante.getMunicipio());
        assertNotEquals("clave-segura", comerciante.getContrasena());
        assertTrue(passwordEncoder.matches("clave-segura", comerciante.getContrasena()));
    }

    @Test
    void registrarComercianteSinDatosObligatorios_Retorna400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles").exists());

        assertEquals(0, comercianteRepository.count());
    }

    @Test
    void registrarComercianteConNitDuplicado_Retorna409() throws Exception {
        registrarComerciante("900123456-7", "compras@mercado.example");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson("900123456-7", "otro@mercado.example")))
                .andExpect(status().isConflict());

        assertEquals(1, comercianteRepository.count());
    }

    @Test
    void registrarComercianteConCorreoDeAgricultor_Retorna409() throws Exception {
        mockMvc.perform(post("/api/v1/agricultores/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombreCompleto": "Ana Productora",
                                  "cedula": "1234567890",
                                  "correo": "compartido@example.com",
                                  "contrasena": "clave-segura",
                                  "ubicacionFinca": "Finca El Valle",
                                  "municipio": "PALMIRA"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson("900123456-7", "compartido@example.com")))
                .andExpect(status().isConflict());

        assertEquals(0, comercianteRepository.count());
    }

    @Test
    void registrarAgricultorConCorreoDeComerciante_Retorna409() throws Exception {
        registrarComerciante("900123456-7", "compartido@example.com");

        mockMvc.perform(post("/api/v1/agricultores/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombreCompleto": "Ana Productora",
                                  "cedula": "1234567890",
                                  "correo": "compartido@example.com",
                                  "contrasena": "clave-segura",
                                  "ubicacionFinca": "Finca El Valle",
                                  "municipio": "PALMIRA"
                                }
                                """))
                .andExpect(status().isConflict());

        assertEquals(0, agricultorRepository.count());
    }

    @Test
    void comerciantePuedeIniciarSesionConTokenDeRolYNoPublicarLotes() throws Exception {
        registrarComerciante("900123456-7", "compras@mercado.example");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "correo": "compras@mercado.example",
                                  "contrasena": "clave-segura"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        String token = response.path("accessToken").asText();
        assertEquals("COMERCIANTE", jwtDecoder.decode(token).getClaimAsString("rol"));

        mockMvc.perform(post("/api/v1/productos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void comercianteConContrasenaIncorrecta_Retorna401() throws Exception {
        registrarComerciante("900123456-7", "compras@mercado.example");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "correo": "compras@mercado.example",
                                  "contrasena": "incorrecta"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    private void registrarComerciante(String nit, String correo) throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson(nit, correo)))
                .andExpect(status().isCreated());
    }

    private String registroJson(String nit, String correo) {
        return """
                {
                  "razonSocial": "Mercado del Valle S.A.S.",
                  "nit": "%s",
                  "correo": "%s",
                  "contrasena": "clave-segura",
                  "telefono": "3001234567",
                  "direccion": "Calle 10 # 20-30",
                  "municipio": "CALI"
                }
                """.formatted(nit, correo);
    }
}
