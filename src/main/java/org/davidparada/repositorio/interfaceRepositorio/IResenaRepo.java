package org.davidparada.repositorio.interfaceRepositorio;

import org.davidparada.modelo.entidad.ResenaEntidad;
import org.davidparada.modelo.formulario.ResenaForm;

import java.util.List;
import java.util.Optional;

public interface IResenaRepo extends ICrud<ResenaEntidad, ResenaForm, Long> {

    /**
     * Lista de reseñas del usuario indicado en el ID.
     *
     * @param idUsuario
     * @return Lista de objetos de la entidad resena.
     */
    List<ResenaEntidad> buscarPorUsuario(Long idUsuario);

    /**
     * Lista de reseñas de un juego indicado en el ID.
     *
     * @param idJuego
     * @return Lista de objetos de la entidad resena.
     */
    List<ResenaEntidad> buscarPorJuego(Long idJuego);

    /**
     * Muestra una reseña con el ID indicado y que pertenezca al usuario indicado en el ID
     *
     * @param idResena
     * @param idUsuario
     * @return Muestra el objeto de la Entidad resena.
     */
    Optional<ResenaEntidad> buscarPorIdYUsuario(Long idResena, Long idUsuario);
}

