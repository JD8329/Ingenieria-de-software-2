package co.uniajc.agrovalle.agrovalleconnect.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.uniajc.agrovalle.agrovalleconnect.dto.MensajeRequest;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Mensaje;
import co.uniajc.agrovalle.agrovalleconnect.models.Notificacion;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.MensajeRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.NotificacionRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MensajeServiceTest {

    @Mock
    private MensajeRepository mensajeRepository;

    @Mock
    private LoteCosechaRepository loteCosechaRepository;

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private MensajeService mensajeService;

    @Test
    void enviarMensajeLoGuardaYNotificaAlAgricultorDelLote() {
        Agricultor agricultor = new Agricultor();
        agricultor.setId(7L);
        LoteCosecha lote = new LoteCosecha();
        lote.setId(15L);
        lote.setAgricultor(agricultor);
        when(loteCosechaRepository.findById(15L)).thenReturn(Optional.of(lote));
        when(mensajeRepository.save(any(Mensaje.class))).thenAnswer(invocacion -> {
            Mensaje mensaje = invocacion.getArgument(0);
            mensaje.setId(21L);
            return mensaje;
        });

        Mensaje guardado = mensajeService.enviar(new MensajeRequest(3L, 15L, "  ¿Cuándo despacha?  "));

        assertEquals(3L, guardado.getComercianteId());
        assertEquals("¿Cuándo despacha?", guardado.getContenido());
        verify(notificacionRepository).save(any(Notificacion.class));
    }
}
