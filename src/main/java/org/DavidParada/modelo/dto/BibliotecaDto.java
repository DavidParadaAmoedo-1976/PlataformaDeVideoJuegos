package org.DavidParada.modelo.dto;

import java.time.Instant;

public record BibliotecaDto(Long idBiblioteca,
                            Long idUsuario,
                            UsuarioDto usuarioDto,
                            Long idJuego,
                            JuegoDto juegoDto,
                            Instant fechaAdquisicion,
                            Double horasDeJuego,
                            Instant ultimaFechaDeJuego,
                            boolean estadoInstalacion) {
}

