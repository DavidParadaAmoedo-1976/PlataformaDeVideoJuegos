package org.davidparada.modelo.mapper;

import org.davidparada.modelo.dto.CompraDto;
import org.davidparada.modelo.entidad.CompraEntidad;
import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.entidad.UsuarioEntidad;

public class CompraEntidadADtoMapper {

    public CompraEntidadADtoMapper() {
    }

    public static CompraDto compraEntidadADto(CompraEntidad compraEntidad,
                                              UsuarioEntidad usuarioEntidad,
                                              JuegoEntidad juegoEntidad) {

        if (compraEntidad == null) {
            return null;
        }

        return new CompraDto(compraEntidad.getIdCompra(),
                compraEntidad.getIdUsuario(),
                UsuarioEntidadADtoMapper.usuarioEntidadADto(usuarioEntidad),
                compraEntidad.getIdJuego(),
                JuegoEntidadADtoMapper.juegoEntidadADto(juegoEntidad),
                compraEntidad.getFechaCompra(),
                compraEntidad.getMetodoPago(),
                compraEntidad.getPrecioBase(),
                compraEntidad.getDescuento(),
                compraEntidad.getEstadoCompra()
        );
    }
}

