package org.DavidParada.modelo.mapper;

import org.DavidParada.modelo.dto.CompraDto;
import org.DavidParada.modelo.entidad.CompraEntidad;
import org.DavidParada.modelo.entidad.JuegoEntidad;
import org.DavidParada.modelo.entidad.UsuarioEntidad;

public class CompraEntidadADtoMapper {

    public CompraEntidadADtoMapper() {
    }

//    public static CompraDto copraEntidadADto(CompraEntidad compraEntidad) {
//        if (compraEntidad == null) {
//            return null;
//        }
//        return new CompraDto(compraEntidad.getIdCompra(),
//                compraEntidad.getIdUsuario(),
//                UsuarioEntidadADtoMapper.usuarioEntidadADto(usuarioEntidad),
//                compraEntidad.getIdJuego(),
//                JuegoEntidadADtoMapper.juegoEntidadADto(juegoEntidad),
//                compraEntidad.getFechaCompra(),
//                compraEntidad.getMetodoPago(),
//                compraEntidad.getPrecioBase(),
//                compraEntidad.getDescuento(),
//                compraEntidad.getEstado()
//        );
//    }

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

