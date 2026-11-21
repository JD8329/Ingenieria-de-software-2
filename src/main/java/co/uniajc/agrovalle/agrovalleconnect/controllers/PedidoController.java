package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.CalificacionRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.CalificacionResponse;
import co.uniajc.agrovalle.agrovalleconnect.dto.DespachoRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.DespachoResponse;
import co.uniajc.agrovalle.agrovalleconnect.dto.PedidoRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.PedidoResponse;
import co.uniajc.agrovalle.agrovalleconnect.dto.SeguimientoResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.Pedido;
import co.uniajc.agrovalle.agrovalleconnect.services.PedidoService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> reservar(@Valid @RequestBody PedidoRequest datos) {
        PedidoResponse respuesta = PedidoResponse.desde(pedidoService.reservar(datos));
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PostMapping("/{id}/despacho")
    public ResponseEntity<DespachoResponse> programarDespacho(
            @PathVariable Long id,
            @RequestParam(required = false) Long agricultorId,
            @Valid @RequestBody DespachoRequest datos) {
        Pedido pedido = pedidoService.programarDespacho(id, datos, agricultorId);
        return ResponseEntity.ok(DespachoResponse.desde(pedido));
    }

    @GetMapping("/{id}/seguimiento")
    public ResponseEntity<SeguimientoResponse> obtenerSeguimiento(@PathVariable Long id) {
        Pedido pedido = pedidoService.obtenerPedido(id);
        return ResponseEntity.ok(SeguimientoResponse.desde(pedido));
    }

    @PostMapping("/{id}/calificar")
    public ResponseEntity<CalificacionResponse> calificarPedido(
            @PathVariable Long id,
            @Valid @RequestBody CalificacionRequest datos) {
        Pedido pedido = pedidoService.calificarPedido(id, datos);
        BigDecimal promedio = pedidoService.obtenerPromedioCalificacionAgricultor(
                pedido.getDetalles().stream()
                        .findFirst()
                        .map(detalle -> detalle.getLote().getAgricultor().getId())
                        .orElseThrow(() -> new IllegalArgumentException("No se pudo determinar el agricultor del pedido")));
        return ResponseEntity.ok(CalificacionResponse.desde(pedido, promedio));
    }
}
