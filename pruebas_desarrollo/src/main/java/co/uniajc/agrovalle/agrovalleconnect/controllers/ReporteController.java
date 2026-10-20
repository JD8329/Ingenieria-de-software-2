package co.uniajc.agrovalle.agrovalleconnect.controllers;

import co.uniajc.agrovalle.agrovalleconnect.dto.VentasMunicipioResponse;
import co.uniajc.agrovalle.agrovalleconnect.services.ReporteService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/ventas-municipio")
    public ResponseEntity<List<VentasMunicipioResponse>> ventasPorMunicipio() {
        return ResponseEntity.ok(reporteService.ventasPorMunicipio());
    }
}
