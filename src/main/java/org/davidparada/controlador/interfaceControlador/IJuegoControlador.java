package org.davidparada.controlador.interfaceControlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.JuegoDto;
import org.davidparada.modelo.enums.ClasificacionJuegoEnum;
import org.davidparada.modelo.enums.EstadoJuegoEnum;
import org.davidparada.modelo.enums.OrdenarJuegosEnum;
import org.davidparada.modelo.formulario.JuegoForm;

import java.util.List;

public interface IJuegoControlador {

    /**
     * Crea un juego a partir de un formulario.
     * @param form
     * @return Muestra un objeto DTO.
     * @throws ValidationException
     */
    JuegoDto crearJuego(JuegoForm form) throws ValidationException;

    // Buscar juegos
    List<JuegoDto> buscarJuegos(
            String titulo,
            String categoria,
            Double precioMin,
            Double precioMax,
            ClasificacionJuegoEnum clasificacion,
            EstadoJuegoEnum estado
    );

    /**
     * Muestra una lista de juegos en el orden especificado.
     * @param orden
     * @return Lista de objetos DTO.
     */
    List<JuegoDto> consultarCatalogo(OrdenarJuegosEnum orden);

    /**
     * Muestra las especificaciones de un juego recibido por ID.
     * @param idJuego
     * @return Muestra un objeto DTO.
     * @throws ValidationException
     */
    JuegoDto consultarDetalles(Long idJuego) throws ValidationException;

    /**
     * Pone el descuento recibido a un juego recibido por ID.
     * @param id
     * @param descuento
     * @throws ValidationException
     */
    void aplicarDescuento(Long id, Integer descuento) throws ValidationException;

    /**
     * Pone el estado recibido a un juego indicado por ID.
     * @param id
     * @param nuevoEstado
     * @throws ValidationException
     */
    void cambiarEstado(Long id, EstadoJuegoEnum nuevoEstado) throws ValidationException;


}
