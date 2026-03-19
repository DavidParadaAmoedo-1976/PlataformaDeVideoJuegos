package org.davidparada.repositorio.interfaceRepositorio;

import org.davidparada.modelo.entidad.BibliotecaEntidad;
import org.davidparada.modelo.formulario.BibliotecaForm;

import java.util.List;
import java.util.Optional;

public interface IBibliotecaRepo extends ICrud<BibliotecaEntidad, BibliotecaForm, Long> {
    /**
     * Buscar biblioteca a partir de un nombre de usuario(Único).
     *
     * @param idUsuario
     * @return Lista de objetos de la Entidad biblioteca.
     */
    List<BibliotecaEntidad> buscarPorUsuario(Long idUsuario);

    /**
     * Buscar biblioteca que tenga el usuario y el juego al que pertenecen los ID enviados
     *
     * @param idUsuario
     * @param idJuego
     * @return Objeto de la Entidad biblioteca
     */
    Optional<BibliotecaEntidad> buscarPorUsuarioYJuego(Long idUsuario, Long idJuego);
}
