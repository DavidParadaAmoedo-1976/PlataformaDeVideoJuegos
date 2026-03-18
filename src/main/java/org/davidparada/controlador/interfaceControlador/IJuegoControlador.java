package org.davidparada.controlador.interfaceControlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.JuegoDto;
import org.davidparada.modelo.enums.ClasificacionJuegoEnum;
import org.davidparada.modelo.enums.EstadoJuegoEnum;
import org.davidparada.modelo.enums.OrdenarJuegosEnum;
import org.davidparada.modelo.formulario.JuegoForm;

import java.util.List;

public interface IJuegoControlador {
    // Anadir Juego
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

    // Consultar catalogo completo
    List<JuegoDto> consultarCatalogo(OrdenarJuegosEnum orden);

    // Consultar detalles de un juego
    JuegoDto consultarDetalles(Long idJuego) throws ValidationException;

    // Aplicar descuento
    void aplicarDescuento(Long id, Integer descuento) throws ValidationException;

    // Cambiar estado del juego
    void cambiarEstado(Long id, EstadoJuegoEnum nuevoEstado) throws ValidationException;

    boolean eliminar(Long id) throws ValidationException;
}
