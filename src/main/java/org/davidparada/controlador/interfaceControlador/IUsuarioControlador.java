package org.davidparada.controlador.interfaceControlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.UsuarioDto;
import org.davidparada.modelo.formulario.UsuarioForm;

public interface IUsuarioControlador {
    UsuarioDto registrarUsuario(UsuarioForm form) throws ValidationException;

    UsuarioDto consultarPerfil(Long idUsuario) throws ValidationException;

    UsuarioDto consultarPerfil(String nombreUsuario);

    void anadirSaldo(Long idUsuario, Double cantidad) throws ValidationException;

    Double consultarSaldo(Long idUsuario) throws ValidationException;
}
