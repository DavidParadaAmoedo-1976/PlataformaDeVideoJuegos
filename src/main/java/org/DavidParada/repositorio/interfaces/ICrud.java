package org.DavidParada.repositorio.interfaces;

import java.util.List;

public interface ICrud<E, F, I> {


    // Crear
    E crear(F formulario);

    // Leer
    E buscarPorId(I id);

    List<E> listarTodos();

    // Modificar
    E actualizar(I id, F formulario);

    // Borrar
    boolean eliminar(I id);
}
