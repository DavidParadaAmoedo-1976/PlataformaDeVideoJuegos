package org.davidparada.repositorio.interfaces;

import java.util.List;
import java.util.Optional;

public interface ICrud<E, F, I> {


    // Crear
    E crear(F formulario);

    // Leer
    Optional <E> buscarPorId(I id);

    List<E> listarTodos();

    // Modificar
    E actualizar(I id, F formulario);

    // Borrar
    boolean eliminar(I id);
}
