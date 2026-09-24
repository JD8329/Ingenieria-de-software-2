package co.uniajc.agrovalle.agrovalleconnect.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.dto.AgricultorRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.services.AgricultorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AgricultorController.class)
class AgricultorControllerTest {

    private static final String JSON_VALIDO = """
            {
              "nombreCompleto": "Maria Perez",
              "cedula": "1130123456",
              "correo": "maria@correo.com",
              "contrasena": "clave12345",
              "telefono": "3001234567",
              "nombreFinca": "Finca La Esperanza",
              "municipio": "DAGUA"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgricultorService agricultorService;

    @Test
    void registroValidoRetorna201SinContrasena() throws Exception {
        Agricultor guardado = new Agricultor();
        guardado.setId(1L);
        guardado.setNombreCompleto("Maria Perez");
        guardado.setCedula("1130123456");
        guardado.setCorreo("maria@correo.com");
        guardado.setContrasena("HASH");
        guardado.setNombreFinca("Finca La Esperanza");
        guardado.setMunicipio(Municipio.DAGUA);
        when(agricultorService.registrar(any(AgricultorRegistroRequest.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/v1/agricultores/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.municipio").value("DAGUA"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());
    }

    @Test
    void registroConCedulaInvalidaRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/agricultores/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO.replace("1130123456", "abc")))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(agricultorService);
    }

    @Test
    void registroConMunicipioFueraDelValleRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/agricultores/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO.replace("DAGUA", "MEDELLIN")))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(agricultorService);
    }
}
