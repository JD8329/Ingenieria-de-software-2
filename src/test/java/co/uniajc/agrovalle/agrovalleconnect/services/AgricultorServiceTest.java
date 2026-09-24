package co.uniajc.agrovalle.agrovalleconnect.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.uniajc.agrovalle.agrovalleconnect.dto.AgricultorRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.exceptions.ConflictoException;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AgricultorServiceTest {

    @Mock
    private AgricultorRepository agricultorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AgricultorService agricultorService;

    private AgricultorRegistroRequest datosValidos() {
        return new AgricultorRegistroRequest("Maria Perez", "1130123456", "maria@correo.com",
                "clave12345", "3001234567", "Finca La Esperanza", Municipio.DAGUA);
    }

    @Test
    void registroValidoGuardaAgricultorConContrasenaCifrada() {
        when(passwordEncoder.encode("clave12345")).thenReturn("HASH");
        when(agricultorRepository.save(any(Agricultor.class))).thenAnswer(inv -> inv.getArgument(0));

        Agricultor resultado = agricultorService.registrar(datosValidos());

        assertEquals("HASH", resultado.getContrasena());
        assertEquals(Rol.AGRICULTOR, resultado.getRol());
        assertEquals(Municipio.DAGUA, resultado.getMunicipio());
    }

    @Test
    void registroConCorreoDuplicadoLanzaConflicto() {
        when(agricultorRepository.existsByCorreo("maria@correo.com")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> agricultorService.registrar(datosValidos()));
        verify(agricultorRepository, never()).save(any());
    }

    @Test
    void registroConCedulaDuplicadaLanzaConflicto() {
        when(agricultorRepository.existsByCedula("1130123456")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> agricultorService.registrar(datosValidos()));
        verify(agricultorRepository, never()).save(any());
    }
}
