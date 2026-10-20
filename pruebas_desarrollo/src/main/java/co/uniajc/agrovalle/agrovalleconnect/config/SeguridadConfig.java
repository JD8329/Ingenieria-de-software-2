package co.uniajc.agrovalle.agrovalleconnect.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SeguridadConfig {

    private static final int MINIMUM_SECRET_LENGTH = 32;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/auth/recuperar-password",
                                "/api/v1/auth/restablecer-password",
                                "/api/v1/agricultores/registro").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/agricultores/*/perfil").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/productos/{loteId}/transacciones")
                        .hasAuthority("ROLE_PRODUCCION")
                        .requestMatchers(HttpMethod.POST, "/api/v1/productos")
                        .hasAuthority("ROLE_PRODUCCION")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/productos/*")
                        .hasAuthority("ROLE_PRODUCCION")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/*")
                        .hasAuthority("ROLE_PRODUCCION")
                        .requestMatchers("/api/v1/carrito/**")
                        .hasAuthority("ROLE_COMERCIANTE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos")
                        .hasAuthority("ROLE_COMERCIANTE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos/*/despacho")
                        .hasAuthority("ROLE_PRODUCCION")
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/pedidos/*/recibir",
                                "/api/v1/pedidos/*/calificar")
                        .hasAuthority("ROLE_COMERCIANTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos")
                        .hasAnyAuthority("ROLE_COMERCIANTE", "ROLE_PRODUCCION")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reportes/ventas-municipio")
                        .hasAuthority("ROLE_PRODUCCION")
                        .requestMatchers(HttpMethod.POST, "/api/v1/mensajes")
                        .hasAuthority("ROLE_COMERCIANTE")
                        .requestMatchers("/api/v1/notificaciones/**", "/api/v1/mensajes/**")
                        .hasAnyAuthority("ROLE_COMERCIANTE", "ROLE_PRODUCCION")
                        .anyRequest().permitAll())
                .oauth2ResourceServer(resourceServer ->
                        resourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String rol = jwt.getClaimAsString("rol");
            return rol == null ? java.util.List.of()
                    : java.util.List.of(new SimpleGrantedAuthority("ROLE_" + rol));
        });
        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.issuer}") String issuer) {
        SecretKey secretKey = secretKey(secret);
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return decoder;
    }

    @Bean
    public JwtEncoder jwtEncoder(@Value("${app.security.jwt.secret}") String secret) {
        SecretKey secretKey = secretKey(secret);
        return new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(secretKey.getEncoded()));
    }

    private SecretKey secretKey(String secret) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < MINIMUM_SECRET_LENGTH) {
            throw new IllegalStateException(
                    "JWT_SECRET debe contener al menos 32 bytes para firmar tokens HS256");
        }
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}
