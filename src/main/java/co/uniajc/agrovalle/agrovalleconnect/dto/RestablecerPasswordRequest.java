package co.uniajc.agrovalle.agrovalleconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestablecerPasswordRequest(
        @NotBlank(message = "El token es obligatorio")
        @Size(max = 128, message = "El token no puede superar 128 caracteres")
        String token,
        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String nuevaContrasena) {
}
