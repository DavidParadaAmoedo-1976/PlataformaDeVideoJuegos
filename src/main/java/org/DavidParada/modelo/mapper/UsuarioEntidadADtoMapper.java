package org.DavidParada.modelo.mapper;

import org.DavidParada.modelo.dto.UsuarioDto;
import org.DavidParada.modelo.entidad.UsuarioEntidad;

public class UsuarioEntidadADtoMapper {

    private UsuarioEntidadADtoMapper() {
    }

    public static UsuarioDto usuarioEntidadADto(UsuarioEntidad usuario) {

        if (usuario == null) return null;

        return new UsuarioDto(
                usuario.getIdUsuario(),
                usuario.getNombreUsuario(),
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getNombreReal(),
                usuario.getPais(),
                usuario.getFechaNacimiento(),
                usuario.getFechaRegistro(),
                usuario.getAvatar(),
                usuario.getSaldo(),
                usuario.getEstadoCuenta()
        );
    }
}
