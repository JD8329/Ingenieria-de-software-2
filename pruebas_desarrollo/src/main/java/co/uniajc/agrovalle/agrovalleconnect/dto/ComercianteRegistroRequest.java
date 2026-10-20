package co.uniajc.agrovalle.agrovalleconnect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ComercianteRegistroRequest(
        @NotBlank(message = "El nombre completo es obligatorio")
        String nombreCompleto,
        @NotBlank(message = "El nombre del negocio es obligatorio")
        String nombreNegocio,
        @NotBlank(message = "El NIT es obligatorio")
        @Pattern(regexp = "[0-9A-Za-z.-]{6,20}", message = "El NIT debe tener entre 6 y 20 caracteres")
        String nit,
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe tener un formato valido")
        String correo,
        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, message = "La contrasena debe tener al menos 8 caracteres")
        String contrasena,
        String telefono) {
}
