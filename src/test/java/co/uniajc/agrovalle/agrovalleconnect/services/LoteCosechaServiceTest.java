package co.uniajc.agrovalle.agrovalleconnect.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.uniajc.agrovalle.agrovalleconnect.events.LoteCosechaPublicadoEvent;
import co.uniajc.agrovalle.agrovalleconnect.exceptions.ConflictoException;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        Long agricultorId = 7L;
        Agricultor agricultor = new Agricultor();
        agricultor.setId(agricultorId);
        LoteCosecha lote = crearLote("Cafe", LocalDate.now());
        when(agricultorRepository.findById(agricultorId)).thenReturn(Optional.of(agricultor));
        when(loteCosechaRepository.save(lote)).thenAnswer(invocation -> {
            lote.setId(11L);
            return lote;
        });

        LoteCosecha resultado = loteCosechaService.publicarLote(agricultorId, lote);

        assertEquals(lote, resultado);
        assertEquals(agricultor, resultado.getAgricultor());
        assertTrue(resultado.getActivo());
        verify(eventPublisher).publishEvent(
                new LoteCosechaPublicadoEvent(11L, agricultorId, "Cafe"));
    }

    @Test
    void rechazaAgricultorInexistenteSinGuardarNiEmitirEvento() {
        LoteCosecha lote = crearLote("Cafe", LocalDate.now());
        when(agricultorRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> loteCosechaService.publicarLote(7L, lote));

        verify(loteCosechaRepository, never()).save(any(LoteCosecha.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void rechazaFechaPasadaSinGuardarNiEmitirEvento() {
        Agricultor agricultor = new Agricultor();
        LoteCosecha lote = crearLote("Cafe", LocalDate.now().minusDays(1));
        when(agricultorRepository.findById(7L)).thenReturn(Optional.of(agricultor));

        assertThrows(IllegalArgumentException.class,
                () -> loteCosechaService.publicarLote(7L, lote));

        verify(loteCosechaRepository, never()).save(any(LoteCosecha.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void publicarLoteDuplicadoDelMismoAgricultorProductoYFecha_LanzaConflicto() {
        Long agricultorId = 1L;
        LocalDate fechaCosecha = LocalDate.now().plusDays(1);
        Agricultor agricultor = new Agricultor();
        LoteCosecha loteNuevo = crearLote("cafe", fechaCosecha);

        when(agricultorRepository.findById(agricultorId)).thenReturn(Optional.of(agricultor));
        when(loteCosechaRepository
                .existsByAgricultorIdAndTipoProductoIgnoreCaseAndFechaCosechaAndActivoTrue(
                        agricultorId, loteNuevo.getTipoProducto(), fechaCosecha))
                .thenReturn(true);

        assertThrows(ConflictoException.class,
                () -> loteCosechaService.publicarLote(agricultorId, loteNuevo));

        verify(loteCosechaRepository, never()).save(any(LoteCosecha.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void publicarMismoProductoEnFechaDiferente_PermiteGuardar() {
        Long agricultorId = 1L;
        LocalDate fechaExistente = LocalDate.now().plusDays(1);
        Agricultor agricultor = new Agricultor();
        agricultor.setId(agricultorId);
        LoteCosecha loteNuevo = crearLote("Cafe", fechaExistente.plusDays(1));

        when(agricultorRepository.findById(agricultorId)).thenReturn(Optional.of(agricultor));
        when(loteCosechaRepository
                .existsByAgricultorIdAndTipoProductoIgnoreCaseAndFechaCosechaAndActivoTrue(
                        agricultorId, loteNuevo.getTipoProducto(), loteNuevo.getFechaCosecha()))
                .thenReturn(false);
        when(loteCosechaRepository.save(loteNuevo)).thenAnswer(invocation -> {
            loteNuevo.setId(11L);
            return loteNuevo;
        });

        LoteCosecha resultado = loteCosechaService.publicarLote(agricultorId, loteNuevo);

        assertEquals(loteNuevo, resultado);
        verify(loteCosechaRepository)
                .existsByAgricultorIdAndTipoProductoIgnoreCaseAndFechaCosechaAndActivoTrue(
                        agricultorId, loteNuevo.getTipoProducto(), loteNuevo.getFechaCosecha());
        verify(loteCosechaRepository).save(loteNuevo);
        verify(eventPublisher).publishEvent(
                new LoteCosechaPublicadoEvent(11L, agricultorId, "Cafe"));
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

    private LoteCosecha crearLote(String tipoProducto, LocalDate fechaCosecha) {
        LoteCosecha lote = new LoteCosecha();
        lote.setTipoProducto(tipoProducto);
        lote.setFechaCosecha(fechaCosecha);
        return lote;
    }
}
