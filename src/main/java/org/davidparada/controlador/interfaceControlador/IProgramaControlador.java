package org.davidparada.controlador.interfaceControlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.JuegosPopularesDto;
import org.davidparada.modelo.dto.ReporteUsuariosDto;
import org.davidparada.modelo.dto.ReporteVentasDto;
import org.davidparada.modelo.enums.CriterioPopularidadEnum;

import java.time.Instant;
import java.util.List;

public interface IProgramaControlador {
    ReporteVentasDto generarReporteVentas(Instant inicio,
                                          Instant fin,
                                          Long idJuego,
                                          String desarrollador) throws ValidationException;

    ReporteUsuariosDto generarReporteUsuarios(Instant inicio, Instant fin) throws ValidationException;

    List<JuegosPopularesDto> consultarJuegosMasPopulares(CriterioPopularidadEnum criterio, Integer limite) throws ValidationException;
}
