package org.DavidParada.modelo.mapper;

import org.DavidParada.modelo.dto.ResenaDto;
import org.DavidParada.modelo.entidad.JuegoEntidad;
import org.DavidParada.modelo.entidad.ResenaEntidad;
import org.DavidParada.modelo.entidad.UsuarioEntidad;

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
