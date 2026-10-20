package co.uniajc.agrovalle.agrovalleconnect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;
import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
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
class Sprint4ApiIntegrationTest {

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
    private PedidoRepository pedidoRepository;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private LoteCosechaRepository loteCosechaRepository;

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
    void despachoRecepcionYCalificacionActualizanPedidoYPromedio() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "productor@example.com");
        Comerciante comerciante = guardarComerciante("900123456", "comercio@example.com");
        Pedido primerPedido = guardarPedido(agricultor, comerciante);
        Pedido segundoPedido = guardarPedido(agricultor, comerciante);
        primerPedido.setFechaHora(Instant.now().plusSeconds(10));
        pedidoRepository.save(primerPedido);
        String tokenAgricultor = autenticar(agricultor.getCorreo());
        String tokenComerciante = autenticar(comerciante.getCorreo());

        programarDespacho(tokenAgricultor, primerPedido.getId());
        mockMvc.perform(get("/api/v1/pedidos")
                        .header(HttpHeaders.AUTHORIZATION, tokenComerciante))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ruta").value("Palmira - Cali"))
                .andExpect(jsonPath("$[0].fechaEstimadaRecepcion")
                        .value(LocalDate.now().plusDays(2).toString()));

        mockMvc.perform(post("/api/v1/pedidos/{id}/recibir", primerPedido.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenComerciante))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENTREGADO"));
        mockMvc.perform(post("/api/v1/pedidos/{id}/calificar", primerPedido.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenComerciante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"calificacion": 5, "comentarios": "Entrega puntual"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("FINALIZADO"))
                .andExpect(jsonPath("$.calificacion").value(5))
                .andExpect(jsonPath("$.calificacionPromedioAgricultor").value(5.0));
        mockMvc.perform(post("/api/v1/pedidos/{id}/calificar", primerPedido.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenComerciante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"calificacion": 1, "comentarios": "Intento duplicado"}
                                """))
                .andExpect(status().isConflict());

        programarDespacho(tokenAgricultor, segundoPedido.getId());
        mockMvc.perform(post("/api/v1/pedidos/{id}/recibir", segundoPedido.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenComerciante))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/pedidos/{id}/calificar", segundoPedido.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenComerciante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"calificacion": 3, "comentarios": "Calidad adecuada"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calificacionPromedioAgricultor").value(4.0));

        assertEquals(4.0, agricultorRepository.findById(agricultor.getId())
                .orElseThrow().getCalificacionPromedio());
        assertEquals(EstadoPedido.FINALIZADO, pedidoRepository.findById(primerPedido.getId())
                .orElseThrow().getEstado());
    }

    @Test
    void flujoRechazaTransicionesInvalidasYDatosDeCalificacionFueraDeRango() throws Exception {
        Agricultor agricultor = guardarAgricultor("1234567890", "productor@example.com");
        Agricultor otroAgricultor = guardarAgricultor("1234567891", "otro@example.com");
        Comerciante comerciante = guardarComerciante("900123456", "comercio@example.com");
        Pedido pedido = guardarPedido(agricultor, comerciante);
        String tokenAgricultor = autenticar(agricultor.getCorreo());
        String tokenOtroAgricultor = autenticar(otroAgricultor.getCorreo());
        String tokenComerciante = autenticar(comerciante.getCorreo());

        mockMvc.perform(post("/api/v1/pedidos/{id}/despacho", pedido.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenComerciante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoDespacho()))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/pedidos/{id}/despacho", pedido.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenOtroAgricultor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoDespacho()))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/pedidos/{id}/recibir", pedido.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenAgricultor))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/pedidos/{id}/recibir", pedido.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenComerciante))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/v1/pedidos/{id}/calificar", pedido.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenComerciante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"calificacion": 4, "comentarios": "Aun no recibido"}
                                """))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/v1/pedidos/{id}/calificar", pedido.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenComerciante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"calificacion": 6, "comentarios": "Fuera de rango"}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/pedidos/{id}/despacho", pedido.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenAgricultor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"transportista": "Transportes Valle", "ruta": "Palmira - Cali",
                                 "fechaEstimadaRecepcion": "2020-01-01"}
                                """))
                .andExpect(status().isBadRequest());
    }

    private void programarDespacho(String tokenAgricultor, Long pedidoId) throws Exception {
        mockMvc.perform(post("/api/v1/pedidos/{id}/despacho", pedidoId)
                        .header(HttpHeaders.AUTHORIZATION, tokenAgricultor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoDespacho()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_TRANSITO"))
                .andExpect(jsonPath("$.transportista").value("Transportes Valle"));
    }

    private String cuerpoDespacho() {
        return """
                {"transportista": "Transportes Valle", "ruta": "Palmira - Cali",
                 "fechaEstimadaRecepcion": "%s"}
                """.formatted(LocalDate.now().plusDays(2));
    }

    private Agricultor guardarAgricultor(String cedula, String correo) {
        Agricultor agricultor = new Agricultor();
        agricultor.setNombreCompleto("Productor de prueba");
        agricultor.setCedula(cedula);
        agricultor.setCorreo(correo);
        agricultor.setContrasena(passwordEncoder.encode(PASSWORD));
        agricultor.setMunicipio(Municipio.PALMIRA);
        agricultor.setRol(Rol.PRODUCCION);
        return agricultorRepository.save(agricultor);
    }

    private Comerciante guardarComerciante(String nit, String correo) {
        Comerciante comerciante = new Comerciante();
        comerciante.setNombreCompleto("Comerciante de prueba");
        comerciante.setNombreNegocio("Mercado Central");
        comerciante.setNit(nit);
        comerciante.setCorreo(correo);
        comerciante.setContrasena(passwordEncoder.encode(PASSWORD));
        return comercianteRepository.save(comerciante);
    }

    private Pedido guardarPedido(Agricultor agricultor, Comerciante comerciante) {
        Pedido pedido = new Pedido();
        pedido.setAgricultor(agricultor);
        pedido.setComerciante(comerciante);
        pedido.setEstado(EstadoPedido.RESERVADO);
        pedido.setTotal(1000.0);
        pedido.setFechaHora(Instant.now());
        return pedidoRepository.save(pedido);
    }

    private String autenticar(String correo) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"correo": "%s", "contrasena": "%s"}
                                """.formatted(correo, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode respuesta = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return "Bearer " + respuesta.path("accessToken").asString();
    }
}
