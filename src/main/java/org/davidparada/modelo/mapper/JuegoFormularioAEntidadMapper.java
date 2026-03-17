package org.davidparada.modelo.mapper;

import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.enums.EstadoJuegoEnum;
import org.davidparada.modelo.formulario.JuegoForm;

public class JuegoFormularioAEntidadMapper {

    public static final int DESCUENTO_POR_DEFECTO = 0;

    private JuegoFormularioAEntidadMapper() {
    }

    public static JuegoEntidad crearJuegoEntidad(Long id, JuegoForm form) {
        return new JuegoEntidad(
                id,
                form.getTitulo(),
                form.getDescripcion(),
                form.getDesarrollador(),
                form.getFechaLanzamiento(),
                form.getPrecioBase(),
                DESCUENTO_POR_DEFECTO,
                form.getCategoria(),
                form.getClasificacionPorEdad(),
                form.getIdiomas(),
                EstadoJuegoEnum.DISPONIBLE
        );
    }

    public static JuegoEntidad actualizarJuegoEntidad(Long id, JuegoForm form) {

        return new JuegoEntidad(
                id,
                form.getTitulo(),
                form.getDescripcion(),
                form.getDesarrollador(),
                form.getFechaLanzamiento(),
                form.getPrecioBase(),
                form.getDescuento(),
                form.getCategoria(),
                form.getClasificacionPorEdad(),
                form.getIdiomas(),
                form.getEstado()
        );
    }
}

