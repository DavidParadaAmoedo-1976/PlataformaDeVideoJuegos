package org.DavidParada.repositorio.interfaces;

import org.DavidParada.modelo.entidad.BibliotecaEntidad;
import org.DavidParada.modelo.formulario.BibliotecaForm;

import java.util.List;

public interface IBibliotecaRepo extends ICrud<BibliotecaEntidad, BibliotecaForm, Long> {
    List<BibliotecaEntidad> buscarPorUsuario(Long idUsuario);

    BibliotecaEntidad buscarPorUsuarioYJuego(Long idUsuario, Long idJuego);
}
