package org.davidparada.repositorio.interfaces;

import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.formulario.UsuarioForm;

import java.util.Optional;

public interface IUsuarioRepo extends ICrud<UsuarioEntidad, UsuarioForm, Long> {

    Optional <UsuarioEntidad> buscarPorEmail(String email);

    Optional <UsuarioEntidad> buscarPorNombreUsuario(String nombreUsuario);

}

