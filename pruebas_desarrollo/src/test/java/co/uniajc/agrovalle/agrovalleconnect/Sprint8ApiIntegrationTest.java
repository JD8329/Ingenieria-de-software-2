package co.uniajc.agrovalle.agrovalleconnect;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;
import co.uniajc.agrovalle.agrovalleconnect.models.EstadoPedido;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.models.TokenRestablecimiento;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.ComercianteRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.PedidoRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.TokenRestablecimientoRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class Sprint8ApiIntegrationTest {

    private static final String EMAIL = "productor@agrovalle.test";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgricultorRepository agricultorRepository;

    @Autowired
    private ComercianteRepository comercianteRepository;

    @Autowired
    private LoteCosechaRepository loteCosechaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private TokenRestablecimientoRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JavaMailSender mailSender;

    @BeforeEach
    void limpiarDatos() {
        tokenRepository.deleteAll();
        pedidoRepository.deleteAll();
        loteCosechaRepository.deleteAll();
        comercianteRepository.deleteAll();
        agricultorRepository.deleteAll();
        clearInvocations(mailSender);
    }

    @Test
    void perfilPublicoMuestraCalificacionLotesActivosYSoloDatosPublicos() throws Exception {
        Agricultor agricultor = crearAgricultor(EMAIL);
        Comerciante comerciante = crearComerciante();
        guardarPedido(agricultor, comerciante, 4);
        guardarPedido(agricultor, comerciante, 5);
        guardarLote(agricultor, true);
        guardarLote(agricultor, false);

        mockMvc.perform(get("/api/v1/agricultores/{id}/perfil", agricultor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto").value("Productor de Prueba"))
                .andExpect(jsonPath("$.ubicacionFinca").value("Finca El Valle"))
                .andExpect(jsonPath("$.municipio").value("PALMIRA"))
                .andExpect(jsonPath("$.calificacionPromedio").value(4.5))
                .andExpect(jsonPath("$.lotesActivos.length()").value(1))
                .andExpect(jsonPath("$.cedula").doesNotExist())
                .andExpect(jsonPath("$.correo").doesNotExist())
                .andExpect(jsonPath("$.telefono").doesNotExist());
    }

    @Test
    void perfilDeAgricultorInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/api/v1/agricultores/999/perfil"))
                .andExpect(status().isNotFound());
    }

    @Test
    void solicitudDeRecuperacionEnviaTokenNoRevelaCorreoYPermiteUnSoloUso() throws Exception {
        Agricultor agricultor = crearAgricultor(EMAIL);
        String hashAnterior = agricultor.getContrasena();

        mockMvc.perform(post("/api/v1/auth/recuperar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"correo":"PRODUCTOR@agrovalle.test"}
                                """))
                .andExpect(status().isAccepted());

        org.mockito.ArgumentCaptor<SimpleMailMessage> mensajeCaptor =
                org.mockito.ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(mensajeCaptor.capture());
        SimpleMailMessage mensaje = mensajeCaptor.getValue();
        assertTrue(mensaje.getText().contains("antes de 30 minutos"));
        String token = extraerToken(mensaje.getText());
        TokenRestablecimiento registro = tokenRepository.findAll().get(0);
        assertNotEquals(token, registro.getTokenHash());
        assertTrue(registro.getExpiraEn().isAfter(Instant.now()));
        assertTrue(registro.getExpiraEn().isBefore(Instant.now().plusSeconds(31 * 60)));

        mockMvc.perform(post("/api/v1/auth/restablecer-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"%s","nuevaContrasena":"nueva-clave-segura"}
                                """.formatted(token)))
                .andExpect(status().isNoContent());

        Agricultor actualizado = agricultorRepository.findById(agricultor.getId()).orElseThrow();
        assertNotEquals(hashAnterior, actualizado.getContrasena());
        assertTrue(passwordEncoder.matches("nueva-clave-segura", actualizado.getContrasena()));
        assertTrue(tokenRepository.findAll().get(0).getUsadoEn() != null);

        mockMvc.perform(post("/api/v1/auth/restablecer-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"%s","nuevaContrasena":"otra-clave-segura"}
                                """.formatted(token)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void solicitudDeCorreoDesconocidoRespondeIgualSinEnviarMensaje() throws Exception {
        mockMvc.perform(post("/api/v1/auth/recuperar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"correo":"no-existe@agrovalle.test"}
                                """))
                .andExpect(status().isAccepted());

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
        assertTrue(tokenRepository.findAll().isEmpty());
    }

    @Test
    void comercianteTambienPuedeRestablecerConTokenVigente() throws Exception {
        Comerciante comerciante = crearComerciante();
        mockMvc.perform(post("/api/v1/auth/recuperar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"correo":"comerciante@agrovalle.test"}
                                """))
                .andExpect(status().isAccepted());

        org.mockito.ArgumentCaptor<SimpleMailMessage> mensajeCaptor =
                org.mockito.ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(mensajeCaptor.capture());
        String token = extraerToken(mensajeCaptor.getValue().getText());
        mockMvc.perform(post("/api/v1/auth/restablecer-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"%s","nuevaContrasena":"clave-nueva-para-comercio"}
                                """.formatted(token)))
                .andExpect(status().isNoContent());

        Comerciante actualizado = comercianteRepository.findById(comerciante.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("clave-nueva-para-comercio", actualizado.getContrasena()));
    }

    @Test
    void tokenExpiradoNoPermiteCambiarLaContrasena() throws Exception {
        crearAgricultor(EMAIL);
        mockMvc.perform(post("/api/v1/auth/recuperar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"correo":"productor@agrovalle.test"}
                                """))
                .andExpect(status().isAccepted());
        org.mockito.ArgumentCaptor<SimpleMailMessage> mensajeCaptor =
                org.mockito.ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(mensajeCaptor.capture());
        String token = extraerToken(mensajeCaptor.getValue().getText());
        TokenRestablecimiento registro = tokenRepository.findAll().get(0);
        registro.setExpiraEn(Instant.now().minusSeconds(1));
        tokenRepository.save(registro);

        mockMvc.perform(post("/api/v1/auth/restablecer-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"%s","nuevaContrasena":"clave-nueva-segura"}
                                """.formatted(token)))
                .andExpect(status().isBadRequest());
    }

    private Agricultor crearAgricultor(String correo) {
        Agricultor agricultor = new Agricultor();
        agricultor.setNombreCompleto("Productor de Prueba");
        agricultor.setCedula("12345678");
        agricultor.setCorreo(correo);
        agricultor.setContrasena(passwordEncoder.encode("clave-inicial-segura"));
        agricultor.setUbicacionFinca("Finca El Valle");
        agricultor.setMunicipio(Municipio.PALMIRA);
        agricultor.setRol(Rol.PRODUCCION);
        return agricultorRepository.save(agricultor);
    }

    private Comerciante crearComerciante() {
        Comerciante comerciante = new Comerciante();
        comerciante.setNombreCompleto("Comerciante de Prueba");
        comerciante.setNombreNegocio("Mercado de Prueba");
        comerciante.setNit("900123456");
        comerciante.setCorreo("comerciante@agrovalle.test");
        comerciante.setContrasena(passwordEncoder.encode("clave-inicial-comerciante"));
        return comercianteRepository.save(comerciante);
    }

    private void guardarPedido(Agricultor agricultor, Comerciante comerciante, int calificacion) {
        Pedido pedido = new Pedido();
        pedido.setAgricultor(agricultor);
        pedido.setComerciante(comerciante);
        pedido.setEstado(EstadoPedido.FINALIZADO);
        pedido.setTotal(100.0);
        pedido.setFechaHora(Instant.now());
        pedido.setCalificacion(calificacion);
        pedidoRepository.save(pedido);
    }

    private void guardarLote(Agricultor agricultor, boolean activo) {
        LoteCosecha lote = new LoteCosecha();
        lote.setTipoProducto("Tomate");
        lote.setCategoria("Hortalizas");
        lote.setCantidadKg(100.0);
        lote.setPrecioUnitario(2500.0);
        lote.setFechaCosecha(LocalDate.now());
        lote.setAgricultor(agricultor);
        lote.setActivo(activo);
        loteCosechaRepository.save(lote);
    }

    private String extraerToken(String contenido) {
        Matcher matcher = Pattern.compile("token=([A-Za-z0-9_-]+)").matcher(contenido);
        assertTrue(matcher.find(), "El correo debe incluir el token de restablecimiento");
        return matcher.group(1);
    }
}
