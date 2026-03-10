package org.DavidParada.modelo.mapper;

import org.DavidParada.modelo.entidad.UsuarioEntidad;
import org.DavidParada.modelo.enums.EstadoCuentaEnum;
import org.DavidParada.modelo.formulario.UsuarioForm;

import java.time.Instant;
import java.time.LocalDate;

public class UsuarioFormularioAEntidadMapper {

    private UsuarioFormularioAEntidadMapper() {
    }

    public static UsuarioEntidad crearUsuarioEntidad(Long id, UsuarioForm form) {
        return new UsuarioEntidad(
                id,
                form.getNombreUsuario(),
                form.getEmail(),
                form.getPassword(),
                form.getNombreReal(),
                form.getPais(),
                form.getFechaNacimiento(),
                Instant.now(),
                form.getAvatar(),
                0.0,
                EstadoCuentaEnum.ACTIVA
        );
    }

    public static UsuarioEntidad actualizarUsuarioEntidad(Long id, UsuarioForm form) {

        return new UsuarioEntidad(
                id,
                form.getNombreUsuario(),
                form.getEmail(),
                form.getPassword(),
                form.getNombreReal(),
                form.getPais(),
                form.getFechaNacimiento(),
                form.getFechaRegistro(),
                form.getAvatar(),
                form.getSaldo(),
                form.getEstadoCuenta()
        );
    }
}

