package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.CalificacionRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.DespachoRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.PedidoResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.services.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<List<PedidoResponse>> checkout(@AuthenticationPrincipal Jwt jwt) {
        List<PedidoResponse> pedidos = pedidoService.checkout(Long.valueOf(jwt.getSubject())).stream()
                .map(PedidoResponse::desde)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidos);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listar(@AuthenticationPrincipal Jwt jwt) {
        Rol rol = Rol.valueOf(jwt.getClaimAsString("rol"));
        return ResponseEntity.ok(pedidoService.listar(Long.valueOf(jwt.getSubject()), rol).stream()
                .map(PedidoResponse::desde)
                .toList());
    }

    @PostMapping("/{pedidoId}/despacho")
    public ResponseEntity<PedidoResponse> programarDespacho(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long pedidoId,
            @Valid @RequestBody DespachoRequest request) {
        return ResponseEntity.ok(PedidoResponse.desde(pedidoService.programarDespacho(
                Long.valueOf(jwt.getSubject()), pedidoId, request)));
    }

    @PostMapping("/{pedidoId}/recibir")
    public ResponseEntity<PedidoResponse> confirmarRecepcion(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long pedidoId) {
        return ResponseEntity.ok(PedidoResponse.desde(pedidoService.confirmarRecepcion(
                Long.valueOf(jwt.getSubject()), pedidoId)));
    }

    @PostMapping("/{pedidoId}/calificar")
    public ResponseEntity<PedidoResponse> calificar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long pedidoId,
            @Valid @RequestBody CalificacionRequest request) {
        return ResponseEntity.ok(PedidoResponse.desde(pedidoService.calificar(
                Long.valueOf(jwt.getSubject()), pedidoId, request)));
    }
}
