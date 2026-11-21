package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ComercianteRegistroRequest(
        @NotBlank(message = "La razon social es obligatoria")
        @Size(max = 150, message = "La razon social no puede superar 150 caracteres")
        String razonSocial,
        @NotBlank(message = "El NIT es obligatorio")
        @Size(max = 20, message = "El NIT no puede superar 20 caracteres")
        String nit,
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe tener un formato valido")
        String correo,
        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = MINIMUM_PASSWORD_LENGTH,
                message = "La contrasena debe tener al menos 8 caracteres")
        String contrasena,
        @NotBlank(message = "El telefono es obligatorio")
        @Size(max = 20, message = "El telefono no puede superar 20 caracteres")
        String telefono,
        @NotBlank(message = "La direccion es obligatoria")
        @Size(max = 250, message = "La direccion no puede superar 250 caracteres")
        String direccion,
        @NotNull(message = "El municipio es obligatorio")
        Municipio municipio) {

    private static final int MINIMUM_PASSWORD_LENGTH = 8;
}
