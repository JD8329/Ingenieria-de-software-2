package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AgricultorRegistroRequest(
        @NotBlank(message = "El nombre completo es obligatorio")
        String nombreCompleto,
        @NotBlank(message = "La cedula es obligatoria")
        @Pattern(regexp = "\\d{6,10}", message = "La cedula debe tener entre 6 y 10 digitos")
        String cedula,
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe tener un formato valido")
        String correo,
        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, message = "La contrasena debe tener minimo 8 caracteres")
        String contrasena,
        String telefono,
        @NotBlank(message = "El nombre de la finca es obligatorio")
        String nombreFinca,
        @NotNull(message = "El municipio es obligatorio")
        Municipio municipio) {
}
