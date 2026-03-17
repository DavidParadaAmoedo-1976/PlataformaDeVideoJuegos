package org.davidparada.modelo.mapper;

import org.davidparada.modelo.entidad.BibliotecaEntidad;
import org.davidparada.modelo.formulario.BibliotecaForm;

import java.time.Instant;

public class BibliotecaFormularioAEntidadMapper {

    public static final double HORAS_DE_JUEGO_POR_DEFECTO = 0.0;

    private BibliotecaFormularioAEntidadMapper() {
    }

    public static BibliotecaEntidad crearBibliotecaEntidad(Long id, BibliotecaForm form) {

        return new BibliotecaEntidad(
                id,
                form.getIdUsuario(),
                form.getIdJuego(),
                Instant.now(),     // FechaAdquisicion automática
                HORAS_DE_JUEGO_POR_DEFECTO,                 // HorasDeJuego inicial
                null,                // "ultimaFechaDeJuego" -> aún no ha jugado
                false                // No instalado por defecto
        );
    }

    public static BibliotecaEntidad actualizarBibliotecaEntidad(Long id, BibliotecaForm form) {
        return new BibliotecaEntidad(
                id,
                form.getIdUsuario(),
                form.getIdJuego(),
                form.getFechaAdquisicion(),
                form.getHorasDeJuego(),
                form.getUltimaFechaDeJuego(),
                form.isEstadoInstalacion()
        );
    }
}
