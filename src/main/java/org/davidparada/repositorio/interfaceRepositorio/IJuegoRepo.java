package org.davidparada.repositorio.interfaceRepositorio;

import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.enums.ClasificacionJuegoEnum;
import org.davidparada.modelo.enums.EstadoJuegoEnum;
import org.davidparada.modelo.formulario.JuegoForm;

import java.util.List;

public interface IJuegoRepo extends ICrud<JuegoEntidad, JuegoForm, Long> {

    /**
     * Comprueba que exista el título del juego.
     * @param titulo
     * @return Confirmación de si existe o no.
     */
    boolean existeTitulo(String titulo);

    /**
     * Muestra los objetos filtrando según los parametros indicados.
     * @param titulo
     * @param categoria
     * @param precioMin
     * @param precioMax
     * @param clasificacion
     * @param estado
     * @return Lista de objetos de la Entidad juego.
     */
    List<JuegoEntidad> buscarConFiltros(
            String titulo,
            String categoria,
            Double precioMin,
            Double precioMax,
            ClasificacionJuegoEnum clasificacion,
            EstadoJuegoEnum estado
    );

}

