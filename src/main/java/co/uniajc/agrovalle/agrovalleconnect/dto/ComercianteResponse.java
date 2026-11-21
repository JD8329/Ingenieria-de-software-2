package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;

public record ComercianteResponse(
        Long id,
        String razonSocial,
        String nit,
        String correo,
        String telefono,
        String direccion,
        Municipio municipio,
        Rol rol) {

    public static ComercianteResponse desde(Comerciante comerciante) {
        return new ComercianteResponse(
                comerciante.getId(),
                comerciante.getRazonSocial(),
                comerciante.getNit(),
                comerciante.getCorreo(),
                comerciante.getTelefono(),
                comerciante.getDireccion(),
                comerciante.getMunicipio(),
                Rol.COMERCIANTE);
    }
}
