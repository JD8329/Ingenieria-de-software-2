package co.uniajc.agrovalle.agrovalleconnect.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.dto.ReporteVentasMunicipioResponse;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.services.ReporteService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReporteController.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReporteService reporteService;

    @Test
    void consultaReporteVentasMunicipioRetorna200() throws Exception {
        when(reporteService.ventasPorMunicipio()).thenReturn(List.of(
                new ReporteVentasMunicipioResponse(
                        Municipio.PALMIRA,
                        2,
                        new BigDecimal("35.00"),
                        new BigDecimal("87500.00"))));

        mockMvc.perform(get("/api/v1/reportes/ventas-municipio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].municipio").value("PALMIRA"))
                .andExpect(jsonPath("$[0].cantidadPedidos").value(2))
                .andExpect(jsonPath("$[0].cantidadKg").value(35.00))
                .andExpect(jsonPath("$[0].totalVentas").value(87500.00));
    }
}
