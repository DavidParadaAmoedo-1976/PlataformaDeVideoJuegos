package org.davidparada.controlador.interfaceControlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.UsuarioDto;
import org.davidparada.modelo.formulario.UsuarioForm;

public interface IUsuarioControlador {

    /**
     * Crea un usuario nuevo a partir de un formulario.
     *
     * @param form
     * @return Lo muestra en un Objeto DTO.
     * @throws ValidationException
     */
    UsuarioDto registrarUsuario(UsuarioForm form) throws ValidationException;

    /**
     * Muestra el perfil de un usuario a partir de su ID que es único.
     *
     * @param idUsuario
     * @return Lo muestra en un objeto DTO.
     * @throws ValidationException
     */
    UsuarioDto consultarPerfil(Long idUsuario) throws ValidationException;

    /**
     * Muestra el perfil de un usuario a partir de su nombre de usuario que es único.
     *
     * @param nombreUsuario
     * @return Lo muestra en un objeto DTO.
     */
    UsuarioDto consultarPerfil(String nombreUsuario);

    /**
     * Añade la cantidad recibida por parametro al saldo del usuario perteneciente al ID recibido.
     *
     * @param idUsuario
     * @param cantidad
     * @throws ValidationException
     */
    void anadirSaldo(Long idUsuario, Double cantidad) throws ValidationException;

    /**
     * Devuelve el saldo en cartera del usuario al que pertenece el ID recibido.
     *
     * @param idUsuario
     * @return saldo en formato double.
     * @throws ValidationException
     */
    Double consultarSaldo(Long idUsuario) throws ValidationException;
}
