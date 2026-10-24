package co.uniajc.agrovalle.agrovalleconnect.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

@ExtendWith(MockitoExtension.class)
class LoteCosechaServiceTest {

    @Mock
    private LoteCosechaRepository loteCosechaRepository;

    @Mock
    private AgricultorRepository agricultorRepository;

    @InjectMocks
    private LoteCosechaService loteCosechaService;

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
    }

    @Test
    void publicarMismoProductoEnFechaDiferente_PermiteGuardar() {
        Long agricultorId = 1L;
        LocalDate fechaExistente = LocalDate.now().plusDays(1);
        Agricultor agricultor = new Agricultor();
        LoteCosecha loteNuevo = crearLote("Cafe", fechaExistente.plusDays(1));

        when(agricultorRepository.findById(agricultorId)).thenReturn(Optional.of(agricultor));
        when(loteCosechaRepository
                .existsByAgricultorIdAndTipoProductoIgnoreCaseAndFechaCosechaAndActivoTrue(
                        agricultorId, loteNuevo.getTipoProducto(), loteNuevo.getFechaCosecha()))
                .thenReturn(false);
        when(loteCosechaRepository.save(loteNuevo)).thenReturn(loteNuevo);

        LoteCosecha resultado = loteCosechaService.publicarLote(agricultorId, loteNuevo);

        assertSame(loteNuevo, resultado);
        verify(loteCosechaRepository)
                .existsByAgricultorIdAndTipoProductoIgnoreCaseAndFechaCosechaAndActivoTrue(
                        agricultorId, loteNuevo.getTipoProducto(), loteNuevo.getFechaCosecha());
        verify(loteCosechaRepository).save(loteNuevo);
    }

    private LoteCosecha crearLote(String tipoProducto, LocalDate fechaCosecha) {
        LoteCosecha lote = new LoteCosecha();
        lote.setTipoProducto(tipoProducto);
        lote.setFechaCosecha(fechaCosecha);
        return lote;
    }
}
