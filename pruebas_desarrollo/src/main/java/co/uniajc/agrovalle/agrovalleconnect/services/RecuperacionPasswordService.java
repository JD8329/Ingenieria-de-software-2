package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.exceptions.RecursoNoEncontradoException;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;
import co.uniajc.agrovalle.agrovalleconnect.models.TipoCuenta;
import co.uniajc.agrovalle.agrovalleconnect.models.TokenRestablecimiento;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.ComercianteRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.TokenRestablecimientoRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecuperacionPasswordService {

    private static final int TOKEN_BYTES = 32;
    private static final int TOKEN_VALIDEZ_MINUTOS = 30;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AgricultorRepository agricultorRepository;
    private final ComercianteRepository comercianteRepository;
    private final TokenRestablecimientoRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final String baseUrl;
    private final String remitente;

    public RecuperacionPasswordService(
            AgricultorRepository agricultorRepository,
            ComercianteRepository comercianteRepository,
            TokenRestablecimientoRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            @Value("${app.password-reset.base-url}") String baseUrl,
            @Value("${app.password-reset.sender}") String remitente) {
        this.agricultorRepository = agricultorRepository;
        this.comercianteRepository = comercianteRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.baseUrl = baseUrl;
        this.remitente = remitente;
    }

    @Transactional
    public void solicitarRestablecimiento(String correoSolicitado) {
        String correo = correoSolicitado.trim().toLowerCase(Locale.ROOT);
        TipoCuenta tipoCuenta;
        if (agricultorRepository.findByCorreoIgnoreCase(correo).isPresent()) {
            tipoCuenta = TipoCuenta.AGRICULTOR;
        } else if (comercianteRepository.findByCorreoIgnoreCase(correo).isPresent()) {
            tipoCuenta = TipoCuenta.COMERCIANTE;
        } else {
            return;
        }

        String token = generarToken();
        tokenRepository.deleteByCorreoIgnoreCaseAndTipoCuenta(correo, tipoCuenta);
        TokenRestablecimiento registro = new TokenRestablecimiento();
        registro.setTokenHash(hashToken(token));
        registro.setCorreo(correo);
        registro.setTipoCuenta(tipoCuenta);
        registro.setExpiraEn(Instant.now().plus(TOKEN_VALIDEZ_MINUTOS, ChronoUnit.MINUTES));
        tokenRepository.save(registro);
        enviarCorreo(correo, token);
    }

    @Transactional
    public void restablecerPassword(String tokenIngresado, String nuevaContrasena) {
        TokenRestablecimiento token = tokenRepository
                .buscarPorHashConBloqueo(hashToken(tokenIngresado))
                .filter(registro -> registro.getUsadoEn() == null)
                .filter(registro -> registro.getExpiraEn().isAfter(Instant.now()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "El token de restablecimiento no es valido o ha expirado"));

        String contrasenaCifrada = passwordEncoder.encode(nuevaContrasena);
        if (token.getTipoCuenta() == TipoCuenta.AGRICULTOR) {
            Agricultor agricultor = agricultorRepository.findByCorreoIgnoreCase(token.getCorreo())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada"));
            agricultor.setContrasena(contrasenaCifrada);
            agricultorRepository.save(agricultor);
        } else {
            Comerciante comerciante = comercianteRepository.findByCorreoIgnoreCase(token.getCorreo())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada"));
            comerciante.setContrasena(contrasenaCifrada);
            comercianteRepository.save(comerciante);
        }
        token.setUsadoEn(Instant.now());
        tokenRepository.save(token);
    }

    private void enviarCorreo(String correo, String token) {
        String separador = baseUrl.contains("?") ? "&" : "?";
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(correo);
        mensaje.setSubject("Restablecimiento de contraseña - AgroValle Connect");
        mensaje.setText("Para restablecer tu contraseña, abre este enlace antes de 30 minutos:\n\n"
                + baseUrl + separador + "token=" + token
                + "\n\nSi no solicitaste este cambio, puedes ignorar este mensaje.");
        mailSender.send(mensaje);
    }

    private String generarToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("No esta disponible SHA-256", exception);
        }
    }
}
