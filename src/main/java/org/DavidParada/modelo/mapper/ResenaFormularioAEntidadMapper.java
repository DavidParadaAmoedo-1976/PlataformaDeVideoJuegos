package org.DavidParada.modelo.mapper;

import org.DavidParada.modelo.entidad.ResenaEntidad;
import org.DavidParada.modelo.enums.EstadoPublicacionEnum;
import org.DavidParada.modelo.formulario.ResenaForm;

import java.time.Instant;

public class ResenaFormularioAEntidadMapper {

    public ResenaFormularioAEntidadMapper() {
    }

    public static ResenaEntidad crearReseniaEntidad(Long idResenia, ResenaForm form) {

        return new ResenaEntidad(
                idResenia,
                form.getIdUsuario(),
                form.getIdJuego(),
                form.isRecomendado(),
                form.getTextoResena(),
                form.getCantidadHorasJugadas(),
                Instant.now(),
                Instant.now(),
                EstadoPublicacionEnum.PUBLICADA
        );
    }

    public static ResenaEntidad actualizarReseniaEntidad(Long idResenia, ResenaForm form) {

        return new ResenaEntidad(
                idResenia,
                form.getIdUsuario(),
                form.getIdJuego(),
                form.isRecomendado(),
                form.getTextoResena(),
                form.getCantidadHorasJugadas(),
                form.getFechaPublicacion(),
                Instant.now(),
                form.getEstadoPublicacion()
        );
    }
}
