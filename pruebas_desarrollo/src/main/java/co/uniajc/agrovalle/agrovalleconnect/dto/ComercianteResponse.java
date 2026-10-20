package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Comerciante;

public record ComercianteResponse(
        Long id,
        String nombreCompleto,
        String nombreNegocio,
        String nit,
        String correo,
        String telefono) {

    public static ComercianteResponse desde(Comerciante comerciante) {
        return new ComercianteResponse(
                comerciante.getId(),
                comerciante.getNombreCompleto(),
                comerciante.getNombreNegocio(),
                comerciante.getNit(),
                comerciante.getCorreo(),
                comerciante.getTelefono());
    }
}
