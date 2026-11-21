package co.uniajc.agrovalle.agrovalleconnect.services;

import co.uniajc.agrovalle.agrovalleconnect.events.CorreoRecuperacionPasswordEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EnvioCorreoRecuperacionService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(EnvioCorreoRecuperacionService.class);

    private final JavaMailSender mailSender;

    private final String correoRemitente;

    private final String urlRestablecimiento;

    public EnvioCorreoRecuperacionService(
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String correoRemitente,
            @Value("${app.password-reset.url}") String urlRestablecimiento) {
        this.mailSender = mailSender;
        this.correoRemitente = correoRemitente;
        this.urlRestablecimiento = urlRestablecimiento;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void enviarCorreo(CorreoRecuperacionPasswordEvent evento) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mensaje, false, StandardCharsets.UTF_8.name());
            helper.setFrom(correoRemitente);
            helper.setTo(evento.correo());
            helper.setSubject("Recuperación de contraseña - AgroValle Connect");
            helper.setText(contenidoCorreo(evento.token()), false);
            mailSender.send(mensaje);
        } catch (MailException | MessagingException excepcion) {
            LOGGER.error("No se pudo enviar el correo de recuperación de contraseña", excepcion);
        }
    }

    private String contenidoCorreo(String token) {
        String enlace = UriComponentsBuilder.fromUriString(urlRestablecimiento)
                .queryParam("token", token)
                .build()
                .encode()
                .toUriString();
        return "Recibimos una solicitud para restablecer tu contraseña.\n\n"
                + "Usa este enlace antes de 30 minutos:\n" + enlace + "\n\n"
                + "Si no solicitaste este cambio, puedes ignorar este mensaje.";
    }
}
