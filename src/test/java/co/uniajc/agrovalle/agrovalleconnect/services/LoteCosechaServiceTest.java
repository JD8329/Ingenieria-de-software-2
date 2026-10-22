package co.uniajc.agrovalle.agrovalleconnect.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.uniajc.agrovalle.agrovalleconnect.events.LoteCosechaPublicadoEvent;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class LoteCosechaServiceTest {

    @Mock
    private LoteCosechaRepository loteCosechaRepository;

    @Mock
    private AgricultorRepository agricultorRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private LoteCosechaService loteCosechaService;

    @Test
    void publicaLoteYEmiteEventoDeDominio() {
        Agricultor agricultor = new Agricultor();
        agricultor.setId(7L);
        LoteCosecha lote = new LoteCosecha();
        lote.setTipoProducto("Cafe");
        lote.setFechaCosecha(LocalDate.now());
        when(agricultorRepository.findById(7L)).thenReturn(Optional.of(agricultor));
        when(loteCosechaRepository.save(lote)).thenAnswer(invocation -> {
            lote.setId(11L);
            return lote;
        });

        LoteCosecha resultado = loteCosechaService.publicarLote(7L, lote);

        assertEquals(lote, resultado);
        assertEquals(agricultor, resultado.getAgricultor());
        assertTrue(resultado.getActivo());
        verify(eventPublisher).publishEvent(
                new LoteCosechaPublicadoEvent(11L, 7L, "Cafe"));
    }

    @Test
    void rechazaAgricultorInexistenteSinGuardarNiEmitirEvento() {
        LoteCosecha lote = new LoteCosecha();
        when(agricultorRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> loteCosechaService.publicarLote(7L, lote));

        verify(loteCosechaRepository, never()).save(any(LoteCosecha.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void rechazaFechaPasadaSinGuardarNiEmitirEvento() {
        Agricultor agricultor = new Agricultor();
        LoteCosecha lote = new LoteCosecha();
        lote.setFechaCosecha(LocalDate.now().minusDays(1));
        when(agricultorRepository.findById(7L)).thenReturn(Optional.of(agricultor));

        assertThrows(IllegalArgumentException.class,
                () -> loteCosechaService.publicarLote(7L, lote));

        verify(loteCosechaRepository, never()).save(any(LoteCosecha.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void consultaListasDeLotesActivos() {
        loteCosechaService.listarLotesActivos();

        verify(loteCosechaRepository).findByActivoTrue();
    }

    @Test
    void consultaLotesActivosDeUnAgricultor() {
        loteCosechaService.listarLotesPorAgricultor(7L);

        verify(loteCosechaRepository).findByAgricultorIdAndActivoTrue(7L);
    }
}
