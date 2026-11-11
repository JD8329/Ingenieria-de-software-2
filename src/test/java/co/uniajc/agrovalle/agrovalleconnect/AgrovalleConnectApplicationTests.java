package co.uniajc.agrovalle.agrovalleconnect;

import static org.junit.jupiter.api.Assertions.assertEquals;

import co.uniajc.agrovalle.agrovalleconnect.dto.PedidoItemRequest;
import co.uniajc.agrovalle.agrovalleconnect.dto.PedidoRequest;
import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.CategoriaProducto;
import co.uniajc.agrovalle.agrovalleconnect.models.LoteCosecha;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.repositories.AgricultorRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.LoteCosechaRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.NotificacionRepository;
import co.uniajc.agrovalle.agrovalleconnect.repositories.PedidoRepository;
import co.uniajc.agrovalle.agrovalleconnect.services.PedidoService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class AgrovalleConnectApplicationTests {

	@Autowired
	private AgricultorRepository agricultorRepository;

	@Autowired
	private LoteCosechaRepository loteCosechaRepository;

	@Autowired
	private NotificacionRepository notificacionRepository;

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private PedidoService pedidoService;

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void checkoutPersistePedidoNotificacionYDescuentaInventario() {
		Agricultor agricultor = new Agricultor();
		agricultor.setNombreCompleto("María Pérez");
		agricultor.setCedula("123456789");
		agricultor.setCorreo("sprint3@example.com");
		agricultor.setContrasena("hash");
		agricultor.setNombreFinca("El Valle");
		agricultor.setMunicipio(Municipio.DAGUA);
		Agricultor guardado = agricultorRepository.save(agricultor);

		LoteCosecha lote = new LoteCosecha();
		lote.setTipoProducto("Mango");
		lote.setCategoria(CategoriaProducto.FRUTAS);
		lote.setCantidadKg(new BigDecimal("30.00"));
		lote.setPrecioUnitario(new BigDecimal("2500.00"));
		lote.setFechaCosecha(LocalDate.now().plusDays(2));
		lote.setAgricultor(guardado);
		lote.setActivo(true);
		LoteCosecha loteGuardado = loteCosechaRepository.save(lote);

		pedidoService.reservar(new PedidoRequest(9L,
				List.of(new PedidoItemRequest(loteGuardado.getId(), new BigDecimal("5.00")))));

		assertEquals(1, pedidoRepository.count());
		assertEquals(new BigDecimal("25.00"),
				loteCosechaRepository.findById(loteGuardado.getId()).orElseThrow().getCantidadKg());
		assertEquals(1, notificacionRepository.findByAgricultorIdOrderByFechaCreacionDesc(guardado.getId()).size());
	}
}
