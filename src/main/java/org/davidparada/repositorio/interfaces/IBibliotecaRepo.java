package org.davidparada.repositorio.interfaces;

import org.davidparada.modelo.entidad.BibliotecaEntidad;
import org.davidparada.modelo.formulario.BibliotecaForm;

import java.util.List;

public interface IBibliotecaRepo extends ICrud<BibliotecaEntidad, BibliotecaForm, Long> {
    List<BibliotecaEntidad> buscarPorUsuario(Long idUsuario);

    BibliotecaEntidad buscarPorUsuarioYJuego(Long idUsuario, Long idJuego);
}
