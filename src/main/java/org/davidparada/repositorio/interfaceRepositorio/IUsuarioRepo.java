package org.davidparada.repositorio.interfaceRepositorio;

import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.formulario.UsuarioForm;

import java.util.Optional;

public interface IUsuarioRepo extends ICrud<UsuarioEntidad, UsuarioForm, Long> {

    /**
     * Muestra un usuario a partir de un mail(Único).
     *
     * @param email
     * @return Muestra un objeto de la Entidad usuario.
     */
    Optional<UsuarioEntidad> buscarPorEmail(String email);

    /**
     * Busca un usuario a partir del nombre de usuario(Único).
     *
     * @param nombreUsuario
     * @return Muestra un objeto de la Entidad usuario.
     */
    Optional<UsuarioEntidad> buscarPorNombreUsuario(String nombreUsuario);

}

