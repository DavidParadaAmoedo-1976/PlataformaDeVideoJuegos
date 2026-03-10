package org.DavidParada.repositorio.interfaces;

import org.DavidParada.modelo.entidad.UsuarioEntidad;
import org.DavidParada.modelo.formulario.UsuarioForm;


public interface IUsuarioRepo extends ICrud<UsuarioEntidad, UsuarioForm, Long> {

    UsuarioEntidad buscarPorEmail(String email);

    UsuarioEntidad buscarPorNombreUsuario(String nombreUsuario);

}

