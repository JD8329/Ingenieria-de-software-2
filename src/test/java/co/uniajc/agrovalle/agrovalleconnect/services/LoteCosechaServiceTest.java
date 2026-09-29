package co.uniajc.agrovalle.agrovalleconnect.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.uniajc.agrovalle.agrovalleconnect.dto.LoteCosechaRequest;
import co.uniajc.agrovalle.agrovalleconnect.exceptions.RecursoNoEncontradoException;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.CategoriaProducto;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoteCosechaServiceTest {

    @Mock
    private LoteCosechaRepository loteCosechaRepository;

    @Mock
    private AgricultorRepository agricultorRepository;

    @InjectMocks
    private LoteCosechaService loteCosechaService;

    private LoteCosechaRequest datos(LocalDate fecha) {
        return new LoteCosechaRequest("Mango", CategoriaProducto.FRUTAS,
                new BigDecimal("500"), new BigDecimal("2500"), fecha);
    }

    @Test
    void publicarLoteValidoAsignaAgricultorYLoDejaActivo() {
        Agricultor agricultor = new Agricultor();
        agricultor.setId(1L);
        when(agricultorRepository.findById(1L)).thenReturn(Optional.of(agricultor));
        when(loteCosechaRepository.save(any(LoteCosecha.class))).thenAnswer(inv -> inv.getArgument(0));

        LoteCosecha resultado = loteCosechaService.publicar(1L, datos(LocalDate.now().plusDays(3)));

        assertEquals(agricultor, resultado.getAgricultor());
        assertEquals(CategoriaProducto.FRUTAS, resultado.getCategoria());
        assertTrue(resultado.getActivo());
    }

    @Test
    void publicarLoteConFechaPasadaLanzaExcepcion() {
        when(agricultorRepository.findById(1L)).thenReturn(Optional.of(new Agricultor()));

        assertThrows(IllegalArgumentException.class,
                () -> loteCosechaService.publicar(1L, datos(LocalDate.now().minusDays(1))));
        verify(loteCosechaRepository, never()).save(any());
    }

    @Test
    void publicarLoteConAgricultorInexistenteLanzaNoEncontrado() {
        when(agricultorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> loteCosechaService.publicar(99L, datos(LocalDate.now().plusDays(3))));
    }
}
