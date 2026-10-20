package co.uniajc.agrovalle.agrovalleconnect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;
import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.models.Notificacion;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import co.uniajc.agrovalle.agrovalleconnect.models.PedidoItem;
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
class Sprint5ApiIntegrationTest {

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
    void cambiosDeEstadoNotificanACompradorYProductor() throws Exception {
        Agricultor agricultor = guardarAgricultor(
                "1234567890", "productor@example.com", Municipio.PALMIRA);
        Comerciante comerciante = guardarComerciante("900123456", "comercio@example.com");
        Pedido pedido = guardarPedido(agricultor, comerciante, EstadoPedido.RESERVADO);
        String tokenAgricultor = autenticar(agricultor.getCorreo());
        String tokenComerciante = autenticar(comerciante.getCorreo());

        mockMvc.perform(post("/api/v1/pedidos/{id}/despacho", pedido.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenAgricultor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"transportista": "Transportes Valle", "ruta": "Palmira - Cali",
                                 "fechaEstimadaRecepcion": "%s"}
                                """.formatted(LocalDate.now().plusDays(2))))
                .andExpect(status().isOk());
        assertNotificaciones(pedido.getId(), agricultor, comerciante, "EN_TRANSITO");

        mockMvc.perform(post("/api/v1/pedidos/{id}/recibir", pedido.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenComerciante))
                .andExpect(status().isOk());
        assertNotificaciones(pedido.getId(), agricultor, comerciante, "ENTREGADO");

        mockMvc.perform(post("/api/v1/pedidos/{id}/calificar", pedido.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenComerciante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"calificacion": 5, "comentarios": "Entrega puntual"}
                                """))
                .andExpect(status().isOk());
        assertNotificaciones(pedido.getId(), agricultor, comerciante, "FINALIZADO");
    }

    @Test
    void reporteAgrupaVentasFinalizadasPorMunicipioYRestringeAcceso() throws Exception {
        Agricultor palmireno = guardarAgricultor(
                "1234567890", "palmira@example.com", Municipio.PALMIRA);
        Agricultor dagueno = guardarAgricultor(
                "1234567891", "dagua@example.com", Municipio.DAGUA);
        Comerciante comerciante = guardarComerciante("900123456", "comercio@example.com");
        LoteCosecha lotePalmira = guardarLote(palmireno);
        LoteCosecha loteDagua = guardarLote(dagueno);
        guardarPedidoConDetalle(palmireno, comerciante, lotePalmira,
                EstadoPedido.FINALIZADO, 2.5, 1000.0);
        guardarPedidoConDetalle(palmireno, comerciante, lotePalmira,
                EstadoPedido.FINALIZADO, 1.5, 1200.0);
        guardarPedidoConDetalle(dagueno, comerciante, loteDagua,
                EstadoPedido.FINALIZADO, 3.0, 800.0);
        guardarPedidoConDetalle(palmireno, comerciante, lotePalmira,
                EstadoPedido.RESERVADO, 10.0, 1000.0);
        String tokenAgricultor = autenticar(palmireno.getCorreo());
        String tokenComerciante = autenticar(comerciante.getCorreo());

        mockMvc.perform(get("/api/v1/reportes/ventas-municipio"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/reportes/ventas-municipio")
                        .header(HttpHeaders.AUTHORIZATION, tokenComerciante))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/reportes/ventas-municipio")
                        .header(HttpHeaders.AUTHORIZATION, tokenAgricultor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(Municipio.values().length))
                .andExpect(jsonPath("$[0].municipio").value("DAGUA"))
                .andExpect(jsonPath("$[0].pedidosFinalizados").value(1))
                .andExpect(jsonPath("$[0].volumenVendidoKg").value(3.0))
                .andExpect(jsonPath("$[0].totalVentas").value(2400.0))
                .andExpect(jsonPath("$[1].municipio").value("PALMIRA"))
                .andExpect(jsonPath("$[1].pedidosFinalizados").value(2))
                .andExpect(jsonPath("$[1].volumenVendidoKg").value(4.0))
                .andExpect(jsonPath("$[1].totalVentas").value(4300.0))
                .andExpect(jsonPath("$[2].pedidosFinalizados").value(0))
                .andExpect(jsonPath("$[2].volumenVendidoKg").value(0.0))
                .andExpect(jsonPath("$[2].totalVentas").value(0.0));
    }

    private void assertNotificaciones(
            Long pedidoId,
            Agricultor agricultor,
            Comerciante comerciante,
            String estado) {
        List<Notificacion> notificacionesProductor =
                notificacionRepository.findByDestinatarioIdAndDestinatarioRolOrderByFechaHoraDesc(
                        agricultor.getId(), Rol.PRODUCCION);
        List<Notificacion> notificacionesComerciante =
                notificacionRepository.findByDestinatarioIdAndDestinatarioRolOrderByFechaHoraDesc(
                        comerciante.getId(), Rol.COMERCIANTE);
        long notificacionesEsperadas = "EN_TRANSITO".equals(estado) ? 1
                : "ENTREGADO".equals(estado) ? 2 : 3;
        assertEquals(notificacionesEsperadas, notificacionesProductor.size());
        assertEquals(notificacionesEsperadas, notificacionesComerciante.size());
        for (Notificacion notificacion : List.of(
                notificacionesProductor.get(0), notificacionesComerciante.get(0))) {
            assertEquals(TipoNotificacion.PEDIDO_ESTADO_CAMBIADO, notificacion.getTipo());
            assertTrue(notificacion.getContenido().contains("pedido #" + pedidoId));
            assertTrue(notificacion.getContenido().contains(estado));
        }
    }

    private Agricultor guardarAgricultor(String cedula, String correo, Municipio municipio) {
        Agricultor agricultor = new Agricultor();
        agricultor.setNombreCompleto("Productor de prueba");
        agricultor.setCedula(cedula);
        agricultor.setCorreo(correo);
        agricultor.setContrasena(passwordEncoder.encode(PASSWORD));
        agricultor.setMunicipio(municipio);
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

    private Pedido guardarPedido(
            Agricultor agricultor,
            Comerciante comerciante,
            EstadoPedido estado) {
        Pedido pedido = new Pedido();
        pedido.setAgricultor(agricultor);
        pedido.setComerciante(comerciante);
        pedido.setEstado(estado);
        pedido.setTotal(1000.0);
        pedido.setFechaHora(Instant.now());
        return pedidoRepository.save(pedido);
    }

    private void guardarPedidoConDetalle(
            Agricultor agricultor,
            Comerciante comerciante,
            LoteCosecha lote,
            EstadoPedido estado,
            double cantidadKg,
            double precioUnitario) {
        Pedido pedido = guardarPedido(agricultor, comerciante, estado);
        PedidoItem item = new PedidoItem();
        item.setLote(lote);
        item.setCantidadKg(cantidadKg);
        item.setPrecioUnitario(precioUnitario);
        pedido.agregarItem(item);
        pedido.setTotal(cantidadKg * precioUnitario);
        pedidoRepository.save(pedido);
    }

    private LoteCosecha guardarLote(Agricultor agricultor) {
        LoteCosecha lote = new LoteCosecha();
        lote.setTipoProducto("Tomate");
        lote.setCategoria("Primera");
        lote.setCantidadKg(100.0);
        lote.setPrecioUnitario(1000.0);
        lote.setFechaCosecha(LocalDate.now());
        lote.setAgricultor(agricultor);
        return loteCosechaRepository.save(lote);
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
