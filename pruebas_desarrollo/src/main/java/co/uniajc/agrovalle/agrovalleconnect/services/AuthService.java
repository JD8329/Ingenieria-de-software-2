package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.LoginRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.TokenResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.ComercianteRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final long TOKEN_LIFETIME_SECONDS = 3600;

    private final AgricultorRepository agricultorRepository;

    private final ComercianteRepository comercianteRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtEncoder jwtEncoder;

    private final String issuer;

    public AuthService(
            AgricultorRepository agricultorRepository,
            ComercianteRepository comercianteRepository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder,
            @Value("${app.security.jwt.issuer}") String issuer) {
        this.agricultorRepository = agricultorRepository;
        this.comercianteRepository = comercianteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
    }

    public Optional<TokenResponse> login(LoginRequest request) {
        String correo = request.correo().trim();
        Optional<TokenResponse> tokenAgricultor = agricultorRepository.findByCorreoIgnoreCase(correo)
                .filter(agricultor -> passwordEncoder.matches(
                        request.contrasena(), agricultor.getContrasena()))
                .map(agricultor -> crearToken(agricultor.getId(), agricultor.getRol()));
        return tokenAgricultor.or(() -> comercianteRepository.findByCorreoIgnoreCase(correo)
                .filter(comerciante -> passwordEncoder.matches(
                        request.contrasena(), comerciante.getContrasena()))
                .map(comerciante -> crearToken(comerciante.getId(), Rol.COMERCIANTE)));
    }

    private TokenResponse crearToken(Long usuarioId, Rol rol) {
        Instant ahora = Instant.now();
        Instant expiracion = ahora.plus(TOKEN_LIFETIME_SECONDS, ChronoUnit.SECONDS);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(ahora)
                .expiresAt(expiracion)
                .subject(usuarioId.toString())
                .claim("rol", rol.name())
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new TokenResponse(token, "Bearer", TOKEN_LIFETIME_SECONDS);
    }
}
