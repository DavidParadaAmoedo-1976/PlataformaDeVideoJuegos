package org.DavidParada.modelo.mapper;

import org.DavidParada.modelo.dto.BibliotecaDto;
import org.DavidParada.modelo.entidad.BibliotecaEntidad;
import org.DavidParada.modelo.entidad.JuegoEntidad;
import org.DavidParada.modelo.entidad.UsuarioEntidad;

public class BibliotecaEntidadADtoMapper {

    public BibliotecaEntidadADtoMapper() {
    }


    public static BibliotecaDto bibliotecaEntidadADto(BibliotecaEntidad biblioteca,
                                                      UsuarioEntidad usuarioEntidad,
                                                      JuegoEntidad juegoEntidad) {

        if (biblioteca == null) return null;

        return new BibliotecaDto(
                biblioteca.getIdBiblioteca(),
                biblioteca.getIdUsuario(),
                UsuarioEntidadADtoMapper.usuarioEntidadADto(usuarioEntidad),
                biblioteca.getIdJuego(),
                JuegoEntidadADtoMapper.juegoEntidadADto(juegoEntidad),
                biblioteca.getFechaAdquisicion(),
                biblioteca.getHorasDeJuego(),
                biblioteca.getUltimaFechaDeJuego(),
                biblioteca.isEstadoInstalacion()
        );
    }
}