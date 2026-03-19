package org.davidparada.modelo.mapper;

import org.davidparada.modelo.dto.UsuarioDto;
import org.davidparada.modelo.entidad.UsuarioEntidad;

public class UsuarioEntidadADtoMapper {

    private UsuarioEntidadADtoMapper() {
    }

    public static UsuarioDto usuarioEntidadADto(UsuarioEntidad usuario) {

        if (usuario == null) {
            return null;
        }

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
