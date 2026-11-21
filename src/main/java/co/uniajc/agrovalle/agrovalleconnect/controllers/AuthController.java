package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.ComercianteRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.ComercianteResponse;
import co.uniajc.agrovalle.agrovalleconnect.dto.LoginRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.TokenResponse;
import co.uniajc.agrovalle.agrovalleconnect.services.AuthService;
import co.uniajc.agrovalle.agrovalleconnect.services.ComercianteService;
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

    public AuthController(AuthService authService, ComercianteService comercianteService) {
        this.authService = authService;
        this.comercianteService = comercianteService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        Optional<TokenResponse> token = authService.login(request);
        return token.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/register")
    public ResponseEntity<ComercianteResponse> registrar(
            @Valid @RequestBody ComercianteRegistroRequest request) {
        ComercianteResponse response =
                ComercianteResponse.desde(comercianteService.registrar(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
