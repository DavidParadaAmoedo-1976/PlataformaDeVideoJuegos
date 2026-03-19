package org.davidparada.controlador.interfaceControlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.JuegosPopularesDto;
import org.davidparada.modelo.dto.ReporteUsuariosDto;
import org.davidparada.modelo.dto.ReporteVentasDto;
import org.davidparada.modelo.enums.CriterioPopularidadEnum;

import java.time.Instant;
import java.util.List;

public interface IProgramaControlador {

    /**
     * Muestra un reporte de las ventas de un juego o desarrolador entre unas fechas indicadas
     *
     * @param inicio
     * @param fin
     * @param idJuego
     * @param desarrollador
     * @return muestra un objeto DTO.
     * @throws ValidationException
     */
    ReporteVentasDto generarReporteVentas(Instant inicio,
                                          Instant fin,
                                          Long idJuego,
                                          String desarrollador) throws ValidationException;

    /**
     * Hace un reporte de todos los usuarios entre unas fechas indicadas.
     *
     * @param inicio
     * @param fin
     * @return Muestra un objeto DTO.
     * @throws ValidationException
     */
    ReporteUsuariosDto generarReporteUsuarios(Instant inicio, Instant fin) throws ValidationException;

    /**
     * Muestra una lista de juegos ordenados por un criterio recibido.
     *
     * @param criterio
     * @param limite
     * @return Lista de objetos DTO.
     * @throws ValidationException
     */
    List<JuegosPopularesDto> consultarJuegosMasPopulares(CriterioPopularidadEnum criterio, Integer limite) throws ValidationException;
}
