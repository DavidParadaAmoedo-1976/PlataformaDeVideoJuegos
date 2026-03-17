package org.davidparada.modelo.mapper;

import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.enums.EstadoCuentaEnum;
import org.davidparada.modelo.formulario.UsuarioForm;

import java.time.Instant;

public class UsuarioFormularioAEntidadMapper {

    public static final double SALDO_POR_DEFECTO = 0.0;

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
                SALDO_POR_DEFECTO,
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

