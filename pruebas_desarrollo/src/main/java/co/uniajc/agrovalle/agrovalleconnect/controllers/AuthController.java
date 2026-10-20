package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.LoginRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.ComercianteRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.ComercianteResponse;
import co.uniajc.agrovalle.agrovalleconnect.dto.TokenResponse;
import co.uniajc.agrovalle.agrovalleconnect.dto.RecuperarPasswordRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.RestablecerPasswordRequest;
import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;
import co.uniajc.agrovalle.agrovalleconnect.services.AuthService;
import co.uniajc.agrovalle.agrovalleconnect.services.ComercianteService;
import co.uniajc.agrovalle.agrovalleconnect.services.RecuperacionPasswordService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final ComercianteService comercianteService;
    private final RecuperacionPasswordService recuperacionPasswordService;

    public AuthController(
            AuthService authService,
            ComercianteService comercianteService,
            RecuperacionPasswordService recuperacionPasswordService) {
        this.authService = authService;
        this.comercianteService = comercianteService;
        this.recuperacionPasswordService = recuperacionPasswordService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        Optional<TokenResponse> token = authService.login(request);
        return token.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/register")
    public ResponseEntity<ComercianteResponse> registrarComerciante(
            @Valid @RequestBody ComercianteRegistroRequest request) {
        Comerciante comerciante = comercianteService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ComercianteResponse.desde(comerciante));
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<Void> recuperarPassword(
            @Valid @RequestBody RecuperarPasswordRequest request) {
        recuperacionPasswordService.solicitarRestablecimiento(request.correo());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/restablecer-password")
    public ResponseEntity<Void> restablecerPassword(
            @Valid @RequestBody RestablecerPasswordRequest request) {
        recuperacionPasswordService.restablecerPassword(request.token(), request.nuevaContrasena());
        return ResponseEntity.noContent().build();
    }
}
