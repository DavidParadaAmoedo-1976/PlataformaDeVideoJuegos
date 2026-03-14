package org.davidparada.repositorio.interfaces;

import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.formulario.UsuarioForm;

public interface IUsuarioRepo extends ICrud<UsuarioEntidad, UsuarioForm, Long> {

    UsuarioEntidad buscarPorEmail(String email);

    UsuarioEntidad buscarPorNombreUsuario(String nombreUsuario);

}

