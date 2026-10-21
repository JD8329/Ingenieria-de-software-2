package co.uniajc.agrovalle.agrovalleconnect;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.models.TransaccionPrecio;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.TransaccionPrecioRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class Hu03PreciosPromedioIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransaccionPrecioRepository transaccionPrecioRepository;

    @Autowired
    private AgricultorRepository agricultorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Agricultor agricultorDePrueba;

    @BeforeEach
    void limpiarTransacciones() {
        transaccionPrecioRepository.deleteAll();
        long identificador = System.nanoTime();
        agricultorDePrueba = new Agricultor();
        agricultorDePrueba.setNombreCompleto("Productor HU03");
        agricultorDePrueba.setCedula(Long.toString(identificador));
        agricultorDePrueba.setCorreo("hu03-" + identificador + "@example.com");
        agricultorDePrueba.setContrasena(passwordEncoder.encode("clave-segura"));
        agricultorDePrueba.setUbicacionFinca("Finca El Valle");
        agricultorDePrueba.setMunicipio(Municipio.PALMIRA);
        agricultorDePrueba.setRol(Rol.PRODUCCION);
        agricultorDePrueba = agricultorRepository.save(agricultorDePrueba);
    }

    @Test
    void consultarPromedioConMenosDeCincuentaTransacciones_Retorna422() throws Exception {
        guardarTransacciones(49, "Cafe", new BigDecimal("10000.00"),
                Instant.now().minus(1, ChronoUnit.HOURS));

        mockMvc.perform(get("/api/v1/precios/promedio").param("producto", "cafe"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void consultarPromedioConCincuentaTransacciones_RetornaMediaDeLasUltimas24Horas()
            throws Exception {
        guardarTransacciones(50, "Cafe", new BigDecimal("10000.00"),
                Instant.now().minus(1, ChronoUnit.HOURS));
        guardarTransacciones(1, "Cafe", new BigDecimal("90000.00"),
                Instant.now().minus(25, ChronoUnit.HOURS));
        guardarTransacciones(1, "Platano", new BigDecimal("30000.00"),
                Instant.now().minus(1, ChronoUnit.HOURS));

        mockMvc.perform(get("/api/v1/precios/promedio").param("producto", "cafe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.producto").value("cafe"))
                .andExpect(jsonPath("$.cantidadTransacciones").value(50))
                .andExpect(jsonPath("$.precioPromedioCop").value(10000.00))
                .andExpect(jsonPath("$.periodoHoras").value(24));
    }

    @Test
    void registrarTransaccionSinAutenticacion_Retorna401() throws Exception {
        mockMvc.perform(post("/api/v1/precios/transacciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transaccionJson("Cafe", "PALMIRA", "10000.00")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registrarTransaccionAutenticada_Retorna201YLaPersiste() throws Exception {
        String token = registrarAgricultorYObtenerToken();

        mockMvc.perform(post("/api/v1/precios/transacciones")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transaccionJson("Cafe", "PALMIRA", "12500.50")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.producto").value("Cafe"))
                .andExpect(jsonPath("$.precioUnitarioCop").value(12500.50))
                .andExpect(jsonPath("$.registradaEn").exists());

        assertEquals(1, transaccionPrecioRepository.count());
    }

    @Test
    void registrarTransaccionConDatosInvalidos_Retorna400() throws Exception {
        String token = registrarAgricultorYObtenerToken();

        mockMvc.perform(post("/api/v1/precios/transacciones")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transaccionJson(" ", "PALMIRA", "0")))
                .andExpect(status().isBadRequest());

        assertEquals(0, transaccionPrecioRepository.count());
    }

    private void guardarTransacciones(
            int cantidad, String producto, BigDecimal precio, Instant registradaEn) {
        for (int indice = 0; indice < cantidad; indice++) {
            TransaccionPrecio transaccion = new TransaccionPrecio();
            transaccion.setProducto(producto);
            transaccion.setMunicipio(Municipio.PALMIRA);
            transaccion.setPrecioUnitarioCop(precio);
            transaccion.setRegistradaEn(registradaEn);
            transaccion.setRegistradaPor(agricultorDePrueba);
            transaccionPrecioRepository.save(transaccion);
        }
    }

    private String registrarAgricultorYObtenerToken() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "correo": "%s",
                                  "contrasena": "clave-segura"
                                }
                                """.formatted(agricultorDePrueba.getCorreo())))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode respuesta = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return "Bearer " + respuesta.path("accessToken").asText();
    }

    private String transaccionJson(String producto, String municipio, String precio) {
        return """
                {
                  "producto": "%s",
                  "municipio": "%s",
                  "precioUnitarioCop": %s
                }
                """.formatted(producto, municipio, precio);
    }
}
