package org.davidparada.controlador.interfaceControlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.EstadisticasResenasJuegoDto;
import org.davidparada.modelo.dto.ResenaDto;
import org.davidparada.modelo.enums.OrdenarResenaEnum;

import java.util.List;

public interface IResenaControlador {

    /**
     * Crea una reseña a partir de los datos recibidos por parametros.
     *
     * @param idUsuario
     * @param idJuego
     * @param recomendado
     * @param texto
     * @return Lo muestra en un objeto DTO.
     * @throws ValidationException
     */
    ResenaDto escribirResena(
            Long idUsuario,
            Long idJuego,
            boolean recomendado,
            String texto
    ) throws ValidationException;

    /**
     * Elimina una reseña indicada por el ID recibido y que pertenecea un usuario del que se recibe su ID.
     *
     * @param idResena
     * @param idUsuario
     * @return Indica si la operación a tenido éxito.
     * @throws ValidationException
     */
    ResenaDto eliminarResena(Long idResena, Long idUsuario) throws ValidationException;

    /**
     * Obtiene las reseñas de un juego recibido su ID, muestra los juegos recomendados o no segun el segundo parametro,
     * y los muestra ordenados segun la clasificacion seleccionada en el tercer parametro.
     *
     * @param idJuego
     * @param recomendado
     * @param orden
     * @return Lista de objetos DTO
     * @throws ValidationException
     */
    List<ResenaDto> obtenerResenas(Long idJuego,
                                   boolean recomendado,
                                   OrdenarResenaEnum orden) throws ValidationException;

    /**
     * Oculta una reseña recibido su ID y que pertenece a un usuario que también recibimos su ID.
     *
     * @param idResena
     * @param idUsuario
     * @return Lo muestra en un objeto DTO.
     * @throws ValidationException
     */
    ResenaDto ocultarResena(Long idResena, Long idUsuario) throws ValidationException;

    /**
     * Muestra las estadisticas de las reseñas de un juego del cual recibimos su ID.
     *
     * @param idJuego
     * @return Estrada un objeto DTO.
     * @throws ValidationException
     */
    EstadisticasResenasJuegoDto consultarEstadisticasResenaPorJuego(Long idJuego) throws ValidationException;

    /**
     * Muestra las reseñas de un usuario del cual recibimos su ID.
     *
     * @param idUsuario
     * @return Lista de objetos DTO.
     * @throws ValidationException
     */
    List<ResenaDto> obtenerResenasUsuario(Long idUsuario) throws ValidationException;
}
