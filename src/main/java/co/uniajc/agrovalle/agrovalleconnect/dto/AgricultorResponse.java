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
        String nombreFinca,
        Municipio municipio,
        Rol rol) {

    public static AgricultorResponse desde(Agricultor a) {
        return new AgricultorResponse(a.getId(), a.getNombreCompleto(), a.getCedula(),
                a.getCorreo(), a.getTelefono(), a.getNombreFinca(), a.getMunicipio(), a.getRol());
    }
}
