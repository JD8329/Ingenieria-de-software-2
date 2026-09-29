package co.uniajc.agrovalle.agrovalleconnect.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.dto.LoteCosechaRequest;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.CategoriaProducto;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.services.LoteCosechaService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LoteCosechaController.class)
class LoteCosechaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoteCosechaService loteCosechaService;

    private String jsonLote(LocalDate fecha) {
        return """
                {
                  "tipoProducto": "Mango",
                  "categoria": "FRUTAS",
                  "cantidadKg": 500,
                  "precioUnitario": 2500,
                  "fechaCosecha": "%s"
                }
                """.formatted(fecha);
    }

    @Test
    void publicarLoteValidoRetorna201ConId() throws Exception {
        Agricultor agricultor = new Agricultor();
        agricultor.setId(1L);
        agricultor.setNombreCompleto("Maria Perez");
        agricultor.setMunicipio(Municipio.DAGUA);

        LoteCosecha guardado = new LoteCosecha();
        guardado.setId(10L);
        guardado.setTipoProducto("Mango");
        guardado.setCategoria(CategoriaProducto.FRUTAS);
        guardado.setCantidadKg(new BigDecimal("500"));
        guardado.setPrecioUnitario(new BigDecimal("2500"));
        guardado.setFechaCosecha(LocalDate.now().plusDays(5));
        guardado.setActivo(true);
        guardado.setAgricultor(agricultor);
        when(loteCosechaService.publicar(eq(1L), any(LoteCosechaRequest.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/v1/productos")
                        .param("agricultorId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLote(LocalDate.now().plusDays(5))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.municipio").value("DAGUA"))
                .andExpect(jsonPath("$.agricultor").doesNotExist());
    }

    @Test
    void publicarLoteConFechaPasadaRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                        .param("agricultorId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLote(LocalDate.now().minusDays(1))))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(loteCosechaService);
    }
}
