package co.uniajc.agrovalle.agrovalleconnect;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class Hu04FiltroProductosIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgricultorRepository agricultorRepository;

    @Autowired
    private LoteCosechaRepository loteCosechaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void limpiarLotes() {
        loteCosechaRepository.deleteAll();
    }

    @Test
    void buscarPorMunicipioYCategoria_RetornaSoloLotesActivosCoincidentes() throws Exception {
        guardarLote(Municipio.DAGUA, "Frutas", "Manzana", true);
        guardarLote(Municipio.BUGA, "Frutas", "Banano", true);
        guardarLote(Municipio.DAGUA, "Hortalizas", "Tomate", true);
        guardarLote(Municipio.DAGUA, "Frutas", "Pera", false);

        mockMvc.perform(get("/api/v1/productos/buscar")
                        .param("municipio", "DAGUA")
                        .param("categoria", "frutas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipoProducto").value("Manzana"))
                .andExpect(jsonPath("$[0].categoria").value("Frutas"))
                .andExpect(jsonPath("$[0].municipio").value("DAGUA"))
                .andExpect(jsonPath("$[0].cantidadKg").value(25.0))
                .andExpect(jsonPath("$[0].precioUnitario").value(4800.0))
                .andExpect(jsonPath("$[0].activo").value(true));
    }

    @Test
    void buscarSoloPorCategoria_RetornaLotesDeTodosLosMunicipios() throws Exception {
        guardarLote(Municipio.DAGUA, "Frutas", "Manzana", true);
        guardarLote(Municipio.BUGA, "Frutas", "Banano", true);
        guardarLote(Municipio.PALMIRA, "Hortalizas", "Tomate", true);

        mockMvc.perform(get("/api/v1/productos/buscar").param("categoria", "FRUTAS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void buscarSoloPorMunicipio_RetornaLotesActivosDelMunicipio() throws Exception {
        guardarLote(Municipio.DAGUA, "Frutas", "Manzana", true);
        guardarLote(Municipio.DAGUA, null, "Cafe", true);
        guardarLote(Municipio.BUGA, "Frutas", "Banano", true);

        mockMvc.perform(get("/api/v1/productos/buscar").param("municipio", "DAGUA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void buscarSinFiltros_Retorna400() throws Exception {
        mockMvc.perform(get("/api/v1/productos/buscar"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarConCategoriaVacia_Retorna400() throws Exception {
        mockMvc.perform(get("/api/v1/productos/buscar").param("categoria", " "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarCatalogoConLoteAnteriorSinCategoria_Retorna200() throws Exception {
        guardarLote(Municipio.DAGUA, null, "Cafe", true);

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria").doesNotExist());
    }

    private void guardarLote(
            Municipio municipio, String categoria, String producto, boolean activo) {
        Agricultor agricultor = new Agricultor();
        agricultor.setNombreCompleto("Productor de prueba");
        agricultor.setCedula(Long.toString(
                Math.abs(System.nanoTime()) % 9_999_999_999L));
        agricultor.setCorreo(UUID.randomUUID() + "@example.com");
        agricultor.setContrasena(passwordEncoder.encode("clave-segura"));
        agricultor.setUbicacionFinca("Finca de prueba");
        agricultor.setMunicipio(municipio);
        agricultor.setRol(Rol.PRODUCCION);
        agricultor = agricultorRepository.save(agricultor);

        LoteCosecha lote = new LoteCosecha();
        lote.setTipoProducto(producto);
        lote.setCategoria(categoria);
        lote.setCantidadKg(25.0);
        lote.setPrecioUnitario(4800.0);
        lote.setFechaCosecha(LocalDate.now().plusDays(1));
        lote.setAgricultor(agricultor);
        lote.setActivo(activo);
        loteCosechaRepository.save(lote);
    }
}
