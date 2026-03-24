package org.davidparada.controlador.interfaceControlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.BibliotecaDto;
import org.davidparada.modelo.dto.EstadisticasBibliotecaDto;
import org.davidparada.modelo.enums.OrdenarJuegosBibliotecaEnum;

import java.util.List;

public interface IBibliotecaControlador {

    /**
     * Muestra todos los juegos de un usuario recibido por ID en un orden también recibido.
     *
     * @param idUsuario
     * @param orden
     * @return Lista de objetos DTO.
     * @throws ValidationException
     */
    List<BibliotecaDto> verBiblioteca(Long idUsuario, OrdenarJuegosBibliotecaEnum orden) throws ValidationException;

    /**
     * añade un juego recibido por ID al usuario recibido por ID.
     *
     * @param idUsuario
     * @param idJuego
     * @return muestra un objeto DTO.
     * @throws ValidationException
     */
    BibliotecaDto anadirJuego(Long idUsuario, Long idJuego) throws ValidationException;

    /**
     * Elimina el juego recibido por ID que pertenece al usuario recibido por ID.
     *
     * @param idUsuario
     * @param idJuego
     * @throws ValidationException
     */
    BibliotecaDto eliminarJuego(Long idUsuario, Long idJuego) throws ValidationException;

    /**
     * Añadir tiempo de juego recibido, a un juego recibido por ID que pertenece al usuario recibido por ID
     *
     * @param idUsuario
     * @param idJuego
     * @param horas
     * @throws ValidationException
     */
    BibliotecaDto anadirTiempoDeJuego(Long idUsuario, Long idJuego, double horas) throws ValidationException;

    /**
     * Muestra cuando un usuario recibido jugó por última vez a un juego indicado.
     *
     * @param idUsuario
     * @param idJuego
     * @return Mensaje con los datos.
     * @throws ValidationException
     */
    BibliotecaDto consultarUltimaSesion(Long idUsuario, Long idJuego) throws ValidationException;

    /**
     * Buscar segun estado de instalacion recibido y con el texto indicado en el título.
     *
     * @param idUsuario
     * @param texto
     * @param estadoInstalacion
     * @return Lista de objetos DTO.
     * @throws ValidationException
     */
    List<BibliotecaDto> buscarSegunCriterios(Long idUsuario, String texto, Boolean estadoInstalacion) throws ValidationException;

    /**
     * Muestra las estadisticas de la biblioteca de un usuario.
     *
     * @param idUsuario
     * @return muestra un objeto DTO.
     * @throws ValidationException
     */
    EstadisticasBibliotecaDto estadisticasBiblioteca(Long idUsuario) throws ValidationException;
}
