package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.dto.LoginRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.TokenResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
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

    private final PasswordEncoder passwordEncoder;

    private final JwtEncoder jwtEncoder;

    private final String issuer;

    public AuthService(
            AgricultorRepository agricultorRepository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder,
            @Value("${app.security.jwt.issuer}") String issuer) {
        this.agricultorRepository = agricultorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
    }

    public Optional<TokenResponse> login(LoginRequest request) {
        return agricultorRepository.findByCorreo(request.correo())
                .filter(agricultor -> passwordEncoder.matches(
                        request.contrasena(), agricultor.getContrasena()))
                .map(this::crearToken);
    }

    private TokenResponse crearToken(Agricultor agricultor) {
        Instant ahora = Instant.now();
        Instant expiracion = ahora.plus(TOKEN_LIFETIME_SECONDS, ChronoUnit.SECONDS);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(ahora)
                .expiresAt(expiracion)
                .subject(agricultor.getId().toString())
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new TokenResponse(token, "Bearer", TOKEN_LIFETIME_SECONDS);
    }
}
