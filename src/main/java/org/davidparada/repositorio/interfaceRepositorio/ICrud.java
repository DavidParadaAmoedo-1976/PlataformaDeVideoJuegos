package org.davidparada.repositorio.interfaceRepositorio;

import java.util.List;
import java.util.Optional;

public interface ICrud<E, F, I> {


    // Crear
    /**
     * Crear a partir de un formulario
     * @param formulario
     * @return Entidad
     */
    E crear(F formulario);

    // Leer
    /**
     * Listar a partir de un ID.
     * @param id
     * @return Objeto de la Entidad
     */
    Optional<E> buscarPorId(I id);

    /**
     * Listar todos los resultados.
     * @return Lista de objetos de la Entidad.
     */
    List<E> listarTodos();

    // Modificar
    /**
     * Modificar entidad a partir de un ID y con un formulario nuevo.
     * @param id
     * @param formulario
     * @return Objeto de la Entidad.
     */
    Optional<E> actualizar(I id, F formulario);

    // Borrar
    /**
     * Elimina a partir de un ID.
     * @param id
     * @return Confirmación de que se realizó la operación.
     */
    boolean eliminar(I id);
}
