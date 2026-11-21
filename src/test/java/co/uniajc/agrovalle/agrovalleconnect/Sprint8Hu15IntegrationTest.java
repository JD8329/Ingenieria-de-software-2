package co.uniajc.agrovalle.agrovalleconnect;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.models.TokenRecuperacionPassword;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.ComercianteRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.TokenRecuperacionPasswordRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class Sprint8Hu15IntegrationTest {

    private static final String MENSAJE_RECUPERACION =
            "Si el correo está asociado a una cuenta, recibirás instrucciones para recuperar "
                    + "tu contraseña.";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgricultorRepository agricultorRepository;

    @Autowired
    private ComercianteRepository comercianteRepository;

    @Autowired
    private TokenRecuperacionPasswordRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JavaMailSender mailSender;

    @BeforeEach
    void prepararCorreo() {
        reset(mailSender);
        when(mailSender.createMimeMessage())
                .thenAnswer(invocacion -> new MimeMessage(Session.getInstance(new Properties())));
        tokenRepository.deleteAll();
    }

    @Test
    void solicitudNoRevelaSiElCorreoEstaRegistradoYEnviaEnlace() throws Exception {
        Agricultor agricultor = guardarAgricultor();

        mockMvc.perform(post("/api/v1/auth/recuperar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"correo": "%s"}
                                """.formatted(agricultor.getCorreo())))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.mensaje", equalTo(MENSAJE_RECUPERACION)));

        String token = extraerTokenDelCorreo();
        TokenRecuperacionPassword tokenPersistido =
                tokenRepository.findAll().get(0);
        assertEquals(hash(token), tokenPersistido.getTokenHash());
        assertTrue(tokenPersistido.getExpiraEn().isAfter(Instant.now()));
        assertTrue(tokenPersistido.getExpiraEn().isBefore(Instant.now().plusSeconds(1801)));

        mockMvc.perform(post("/api/v1/auth/recuperar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"correo": "inexistente@example.com"}
                                """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.mensaje", equalTo(MENSAJE_RECUPERACION)));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void nuevaSolicitudInvalidaElEnlaceAnterior() throws Exception {
        Agricultor agricultor = guardarAgricultor();
        String solicitud = """
                {"correo": "%s"}
                """.formatted(agricultor.getCorreo());

        mockMvc.perform(post("/api/v1/auth/recuperar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(solicitud))
                .andExpect(status().isAccepted());
        mockMvc.perform(post("/api/v1/auth/recuperar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(solicitud))
                .andExpect(status().isAccepted());

        ArgumentCaptor<MimeMessage> mensajeCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, timeout(5000).times(2)).send(mensajeCaptor.capture());
        String primerToken = extraerToken(mensajeCaptor.getAllValues().get(0));
        String segundoToken = extraerToken(mensajeCaptor.getAllValues().get(1));
        TokenRecuperacionPassword tokenActual = tokenRepository.findAll().get(0);
        String hashPrimerToken = hash(primerToken);
        String hashSegundoToken = hash(segundoToken);

        assertNotEquals(primerToken, segundoToken);
        assertTrue(tokenActual.getTokenHash().equals(hashPrimerToken)
                || tokenActual.getTokenHash().equals(hashSegundoToken));
        assertEquals(1, tokenRepository.count());

        String tokenVigente = tokenActual.getTokenHash().equals(hashPrimerToken)
                ? primerToken : segundoToken;
        String tokenInvalidado = tokenActual.getTokenHash().equals(hashPrimerToken)
                ? segundoToken : primerToken;
        mockMvc.perform(post("/api/v1/auth/restablecer-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token": "%s", "nuevaContrasena": "nueva-clave-segura"}
                                """.formatted(tokenVigente)))
                .andExpect(status().isNoContent());
        mockMvc.perform(post("/api/v1/auth/restablecer-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token": "%s", "nuevaContrasena": "otra-clave-segura"}
                                """.formatted(tokenInvalidado)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void restableceContrasenaDeAgricultorYElTokenSoloSeUsaUnaVez() throws Exception {
        Agricultor agricultor = guardarAgricultor();
        String token = "token-unico-agricultor";
        guardarToken(token, agricultor.getId(), agricultor.getRol(), Instant.now().plusSeconds(1800));

        mockMvc.perform(post("/api/v1/auth/restablecer-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token": "%s", "nuevaContrasena": "nueva-clave-segura"}
                                """.formatted(token)))
                .andExpect(status().isNoContent());

        Agricultor actualizado = agricultorRepository.findById(agricultor.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("nueva-clave-segura", actualizado.getContrasena()));
        assertFalse(passwordEncoder.matches("clave-anterior", actualizado.getContrasena()));
        assertTrue(tokenRepository.findAll().isEmpty());

        mockMvc.perform(post("/api/v1/auth/restablecer-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token": "%s", "nuevaContrasena": "otra-clave-segura"}
                                """.formatted(token)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void restableceContrasenaDeComerciante() throws Exception {
        Comerciante comerciante = guardarComerciante();
        String token = "token-unico-comerciante";
        guardarToken(token, comerciante.getId(), Rol.COMERCIANTE, Instant.now().plusSeconds(1800));

        mockMvc.perform(post("/api/v1/auth/restablecer-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token": "%s", "nuevaContrasena": "nueva-clave-segura"}
                                """.formatted(token)))
                .andExpect(status().isNoContent());

        Comerciante actualizado =
                comercianteRepository.findById(comerciante.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("nueva-clave-segura", actualizado.getContrasena()));
    }

    @Test
    void enlaceExpiradoNoPermiteCambiarLaContrasena() throws Exception {
        Agricultor agricultor = guardarAgricultor();
        String token = "token-expirado";
        guardarToken(token, agricultor.getId(), agricultor.getRol(), Instant.now().minusSeconds(1));

        mockMvc.perform(post("/api/v1/auth/restablecer-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token": "%s", "nuevaContrasena": "nueva-clave-segura"}
                                """.formatted(token)))
                .andExpect(status().isBadRequest());

        Agricultor sinCambios =
                agricultorRepository.findById(agricultor.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("clave-anterior", sinCambios.getContrasena()));
    }

    private Agricultor guardarAgricultor() {
        String identificador = UUID.randomUUID().toString();
        Agricultor agricultor = new Agricultor();
        agricultor.setNombreCompleto("Agricultor de prueba");
        agricultor.setCedula(Long.toString(Math.abs(System.nanoTime())));
        agricultor.setCorreo("agricultor-" + identificador + "@example.com");
        agricultor.setContrasena(passwordEncoder.encode("clave-anterior"));
        agricultor.setNombreFinca("Finca de prueba");
        agricultor.setMunicipio(Municipio.PALMIRA);
        agricultor.setRol(Rol.PRODUCCION);
        return agricultorRepository.save(agricultor);
    }

    private Comerciante guardarComerciante() {
        String identificador = UUID.randomUUID().toString();
        Comerciante comerciante = new Comerciante();
        comerciante.setRazonSocial("Comerciante de prueba");
        comerciante.setNit(Long.toString(Math.abs(System.nanoTime())));
        comerciante.setCorreo("comerciante-" + identificador + "@example.com");
        comerciante.setContrasena(passwordEncoder.encode("clave-anterior"));
        comerciante.setTelefono("3001234567");
        comerciante.setDireccion("Dirección de prueba");
        comerciante.setMunicipio(Municipio.CALI);
        return comercianteRepository.save(comerciante);
    }

    private void guardarToken(String token, Long usuarioId, Rol rol, Instant expiraEn)
            throws Exception {
        TokenRecuperacionPassword tokenPersistido = new TokenRecuperacionPassword();
        tokenPersistido.setUsuarioId(usuarioId);
        tokenPersistido.setRol(rol);
        tokenPersistido.setTokenHash(hash(token));
        tokenPersistido.setExpiraEn(expiraEn);
        tokenRepository.save(tokenPersistido);
    }

    private String extraerTokenDelCorreo() throws Exception {
        ArgumentCaptor<MimeMessage> mensajeCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, timeout(5000)).send(mensajeCaptor.capture());
        return extraerToken(mensajeCaptor.getValue());
    }

    private String extraerToken(MimeMessage mensaje) throws Exception {
        String contenido = mensaje.getContent().toString();
        Matcher matcher = Pattern.compile("[?&]token=([A-Za-z0-9_-]+)").matcher(contenido);
        assertTrue(matcher.find());
        return matcher.group(1);
    }

    private String hash(String token) throws Exception {
        byte[] bytes = MessageDigest.getInstance("SHA-256")
                .digest(token.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(bytes);
    }
}
