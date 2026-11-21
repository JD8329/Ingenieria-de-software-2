package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.events.CorreoRecuperacionPasswordEvent;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.models.TokenRecuperacionPassword;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.ComercianteRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.TokenRecuperacionPasswordRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RecuperacionPasswordService {

    private static final long MINUTOS_VIGENCIA = 30;

    private static final String MENSAJE_TOKEN_INVALIDO =
            "El enlace de recuperación no es válido o ha expirado";

    private static final SecureRandom GENERADOR_ALEATORIO = new SecureRandom();

    private final AgricultorRepository agricultorRepository;

    private final ComercianteRepository comercianteRepository;

    private final TokenRecuperacionPasswordRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher eventPublisher;

    public RecuperacionPasswordService(
            AgricultorRepository agricultorRepository,
            ComercianteRepository comercianteRepository,
            TokenRecuperacionPasswordRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher) {
        this.agricultorRepository = agricultorRepository;
        this.comercianteRepository = comercianteRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void solicitarRecuperacion(String correo) {
        tokenRepository.deleteAllByExpiraEnBefore(Instant.now());
        Optional<Cuenta> cuenta = agricultorRepository.findByCorreo(correo)
                .map(agricultor -> new Cuenta(
                        agricultor.getId(), agricultor.getCorreo(), agricultor.getRol()))
                .or(() -> comercianteRepository.findByCorreo(correo)
                        .map(comerciante -> new Cuenta(
                                comerciante.getId(), comerciante.getCorreo(), Rol.COMERCIANTE)));
        if (cuenta.isEmpty()) {
            return;
        }

        Cuenta usuario = cuenta.orElseThrow();
        String token = generarToken();
        TokenRecuperacionPassword tokenPersistido = new TokenRecuperacionPassword();
        tokenPersistido.setUsuarioId(usuario.id());
        tokenPersistido.setRol(usuario.rol());
        tokenPersistido.setTokenHash(calcularHash(token));
        tokenPersistido.setExpiraEn(Instant.now().plus(MINUTOS_VIGENCIA, ChronoUnit.MINUTES));

        tokenRepository.deleteAllByRolAndUsuarioId(usuario.rol(), usuario.id());
        tokenRepository.save(tokenPersistido);
        eventPublisher.publishEvent(
                new CorreoRecuperacionPasswordEvent(usuario.correo(), token));
    }

    @Transactional
    public void restablecerContrasena(String token, String nuevaContrasena) {
        tokenRepository.deleteAllByExpiraEnBefore(Instant.now());
        TokenRecuperacionPassword tokenPersistido = tokenRepository
                .findByTokenHashForUpdate(calcularHash(token))
                .filter(registro -> registro.getExpiraEn().isAfter(Instant.now()))
                .orElseThrow(() -> new IllegalArgumentException(MENSAJE_TOKEN_INVALIDO));

        Long usuarioId = tokenPersistido.getUsuarioId();
        Rol rol = tokenPersistido.getRol();
        if (rol == Rol.COMERCIANTE) {
            Comerciante comerciante = comercianteRepository.findById(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException(MENSAJE_TOKEN_INVALIDO));
            comerciante.setContrasena(passwordEncoder.encode(nuevaContrasena));
            comercianteRepository.save(comerciante);
        } else {
            Agricultor agricultor = agricultorRepository.findById(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException(MENSAJE_TOKEN_INVALIDO));
            agricultor.setContrasena(passwordEncoder.encode(nuevaContrasena));
            agricultorRepository.save(agricultor);
        }

        tokenRepository.deleteAllByRolAndUsuarioId(rol, usuarioId);
    }

    private String generarToken() {
        byte[] bytes = new byte[32];
        GENERADOR_ALEATORIO.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String calcularHash(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException excepcion) {
            throw new IllegalStateException("No está disponible SHA-256", excepcion);
        }
    }

    private record Cuenta(Long id, String correo, Rol rol) {
    }
}
