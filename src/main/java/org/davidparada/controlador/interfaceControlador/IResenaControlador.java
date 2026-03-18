package org.davidparada.controlador.interfaceControlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.EstadisticasResenasJuegoDto;
import org.davidparada.modelo.dto.ResenaDto;
import org.davidparada.modelo.enums.OrdenarResenaEnum;

import java.util.List;

public interface IResenaControlador {
    ResenaDto escribirResena(
            Long idUsuario,
            Long idJuego,
            boolean recomendado,
            String texto
    ) throws ValidationException;

    boolean eliminarResena(Long idResena, Long idUsuario) throws ValidationException;

    List<ResenaDto> obtenerResenas(Long idJuego,
                                   boolean recomendado,
                                   OrdenarResenaEnum orden) throws ValidationException;

    ResenaDto ocultarResena(Long idResena, Long idUsuario) throws ValidationException;

    EstadisticasResenasJuegoDto consultarEstadisticasResenaPorJuego(Long idJuego) throws ValidationException;

    List<ResenaDto> obtenerResenasUsuario(Long idUsuario) throws ValidationException;
}
