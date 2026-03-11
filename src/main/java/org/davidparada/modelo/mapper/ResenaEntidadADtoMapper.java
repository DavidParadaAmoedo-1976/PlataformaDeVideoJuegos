package org.davidparada.modelo.mapper;

import org.davidparada.modelo.dto.ResenaDto;
import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.entidad.ResenaEntidad;
import org.davidparada.modelo.entidad.UsuarioEntidad;

public class ResenaEntidadADtoMapper {

    public ResenaEntidadADtoMapper() {
    }

    public static ResenaDto resenaEntidadADto(ResenaEntidad resenaEntidad,
                                              UsuarioEntidad usuarioEntidad,
                                              JuegoEntidad juegoEntidad) {

        if (resenaEntidad == null) {

            return null;
        }

        return new ResenaDto(resenaEntidad.getIdResena(),
                resenaEntidad.getIdUsuario(),
                UsuarioEntidadADtoMapper.usuarioEntidadADto(usuarioEntidad),
                resenaEntidad.getIdJuego(),
                JuegoEntidadADtoMapper.juegoEntidadADto(juegoEntidad),
                resenaEntidad.isRecomendado(),
                resenaEntidad.getTextoResena(),
                resenaEntidad.getCantidadHorasJugadas(),
                resenaEntidad.getFechaPublicacion(),
                resenaEntidad.getFechaUltimaEdicion(),
                resenaEntidad.getEstadoPublicacion()
        );
    }

}
