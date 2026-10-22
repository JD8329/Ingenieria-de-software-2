package co.uniajc.agrovalle.agrovalleconnect.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.uniajc.agrovalle.agrovalleconnect.dto.AgricultorRegistroRequest;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    @Test
    void registraAgricultorConContrasenaCodificada() {
        AgricultorRegistroRequest request = solicitud();
        when(agricultorRepository.existsByCorreo(request.correo())).thenReturn(false);
        when(agricultorRepository.existsByCedula(request.cedula())).thenReturn(false);
        when(passwordEncoder.encode(request.contrasena())).thenReturn("hash-seguro");

        agricultorService.registrarAgricultor(request);

        ArgumentCaptor<Agricultor> captor = ArgumentCaptor.forClass(Agricultor.class);
        verify(agricultorRepository).save(captor.capture());
        assertEquals("hash-seguro", captor.getValue().getContrasena());
        assertEquals(Rol.PRODUCCION, captor.getValue().getRol());
        assertEquals(Municipio.PALMIRA, captor.getValue().getMunicipio());
    }

    @Test
    void rechazaCorreoDuplicadoSinConsultarCedula() {
        AgricultorRegistroRequest request = solicitud();
        when(agricultorRepository.existsByCorreo(request.correo())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> agricultorService.registrarAgricultor(request));

        verify(agricultorRepository, never()).existsByCedula(request.cedula());
        verify(agricultorRepository, never()).save(any(Agricultor.class));
    }

    @Test
    void rechazaCedulaDuplicada() {
        AgricultorRegistroRequest request = solicitud();
        when(agricultorRepository.existsByCorreo(request.correo())).thenReturn(false);
        when(agricultorRepository.existsByCedula(request.cedula())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> agricultorService.registrarAgricultor(request));

        verify(agricultorRepository, never()).save(any(Agricultor.class));
    }

    private AgricultorRegistroRequest solicitud() {
        return new AgricultorRegistroRequest(
                "Ana Productora",
                "1234567890",
                "ana@example.com",
                "clave-segura",
                null,
                "Finca El Valle",
                Municipio.PALMIRA);
    }
}
