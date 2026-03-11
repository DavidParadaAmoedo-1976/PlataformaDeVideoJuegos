package org.davidparada.repositorio.interfaces;

import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.enums.ClasificacionJuegoEnum;
import org.davidparada.modelo.enums.EstadoJuegoEnum;
import org.davidparada.modelo.formulario.JuegoForm;

import java.util.List;

public interface IJuegoRepo extends ICrud<JuegoEntidad, JuegoForm, Long> {

    boolean existeTitulo(String titulo);

    List<JuegoEntidad> buscarConFiltros(
            String titulo,
            String categoria,
            Double precioMin,
            Double precioMax,
            ClasificacionJuegoEnum clasificacion,
            EstadoJuegoEnum estado
    );

}

