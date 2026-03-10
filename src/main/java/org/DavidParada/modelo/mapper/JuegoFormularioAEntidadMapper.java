package org.DavidParada.modelo.mapper;

import org.DavidParada.modelo.entidad.JuegoEntidad;
import org.DavidParada.modelo.enums.EstadoJuegoEnum;
import org.DavidParada.modelo.formulario.JuegoForm;

public class JuegoFormularioAEntidadMapper {

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
                0,
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

