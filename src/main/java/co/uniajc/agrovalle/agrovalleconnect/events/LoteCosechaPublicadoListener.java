package co.uniajc.agrovalle.agrovalleconnect.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class LoteCosechaPublicadoListener {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(LoteCosechaPublicadoListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void registrarPublicacion(LoteCosechaPublicadoEvent event) {
        LOGGER.info("Lote {} de agricultor {} publicado: {}",
                event.loteId(), event.agricultorId(), event.tipoProducto());
    }
}
