package co.uniajc.agrovalle.agrovalleconnect.dto;

import co.uniajc.agrovalle.agrovalleconnect.models.Agricultor;
import co.uniajc.agrovalle.agrovalleconnect.models.Municipio;
import co.uniajc.agrovalle.agrovalleconnect.models.Rol;

public record AgricultorResponse(
        Long id,
        String nombreCompleto,
        String cedula,
        String correo,
        String telefono,
        String ubicacionFinca,
        Municipio municipio,
        Rol rol) {

    public static AgricultorResponse desde(Agricultor agricultor) {
        return new AgricultorResponse(
                agricultor.getId(),
                agricultor.getNombreCompleto(),
                agricultor.getCedula(),
                agricultor.getCorreo(),
                agricultor.getTelefono(),
                agricultor.getUbicacionFinca(),
                agricultor.getMunicipio(),
                agricultor.getRol());
    }
}
