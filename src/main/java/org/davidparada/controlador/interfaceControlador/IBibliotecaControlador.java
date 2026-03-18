package org.davidparada.controlador.interfaceControlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.BibliotecaDto;
import org.davidparada.modelo.dto.EstadisticasBibliotecaDto;
import org.davidparada.modelo.enums.OrdenarJuegosBibliotecaEnum;

import java.util.List;

public interface IBibliotecaControlador {
    List<BibliotecaDto> verBiblioteca(Long idUsuario, OrdenarJuegosBibliotecaEnum orden) throws ValidationException;

    BibliotecaDto anadirJuego(Long idUsuario, Long idJuego) throws ValidationException;

    void eliminarJuego(Long idUsuario, Long idJuego) throws ValidationException;

    void anadirTiempoDeJuego(Long idUsuario, Long idJuego, double horas) throws ValidationException;

    String consultarUltimaSesion(Long idUsuario, Long idJuego) throws ValidationException;

    List<BibliotecaDto> buscarSegunCriterios(Long idUsuario, String texto, Boolean estadoInstalacion) throws ValidationException;

    EstadisticasBibliotecaDto estadisticasBiblioteca(Long idUsuario) throws ValidationException;
}
