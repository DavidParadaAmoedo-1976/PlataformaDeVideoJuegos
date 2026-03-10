package org.DavidParada.modelo.dto;

import org.DavidParada.modelo.enums.EstadoPublicacionEnum;

import java.time.Instant;

public record ResenaDto(Long idResena,
                        Long idUsuario,
                        UsuarioDto usuarioDto,
                        Long idJuego,
                        JuegoDto juegoDto,
                        boolean recomendado,
                        String textoResena,
                        Double cantidadHorasJugadas,
                        Instant fechaPublicacion,
                        Instant fechaUltimaEdicion,
                        EstadoPublicacionEnum estadoPublicacion) {
}
