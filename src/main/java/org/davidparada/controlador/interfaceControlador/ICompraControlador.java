package org.davidparada.controlador.interfaceControlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.CompraDto;
import org.davidparada.modelo.dto.DetallesCompraDto;
import org.davidparada.modelo.dto.FacturaDto;
import org.davidparada.modelo.enums.MetodoPagoEnum;

import java.util.List;

public interface ICompraControlador {

    /**
     * Crea una compra con los datos recibidos.
     * @param idUsuario
     * @param idJuego
     * @param metodoPago
     * @return Muestra la compra creada en un objeto DTO.
     * @throws ValidationException
     */
    CompraDto realizarCompra(
            Long idUsuario,
            Long idJuego,
            MetodoPagoEnum metodoPago
    ) throws ValidationException;

    /**
     * Realiza el pago de la compra recibida con el metodo de pago indicado.
     * @param idCompra
     * @param metodoPago
     * @throws ValidationException
     */
    void procesarPago(Long idCompra, MetodoPagoEnum metodoPago) throws ValidationException;

    /**
     * Muestra todas las compras de un usuario recibido.
     * @param idUsuario
     * @return Lista de objetos DTO.
     * @throws ValidationException
     */
    List<CompraDto> listarCompras(Long idUsuario) throws ValidationException;

    /**
     * Muestra una compra recibida por ID con el usuario también recibido por ID.
     * @param idCompra
     * @param idUsuario
     * @return Muestra un Objeto DTO.
     * @throws ValidationException
     */
    CompraDto consultarCompra(Long idCompra, Long idUsuario) throws ValidationException;

    /**
     * Muestra información de una compra recibida por ID con el usuario también recibido por ID.
     * @param idCompra
     * @param idUsuario
     * @return Muestra un objeto DTO.
     * @throws ValidationException
     */
    DetallesCompraDto detallesDeUnaCompra(Long idCompra, Long idUsuario) throws ValidationException;

    /**
     * Reembolsa el dinero pagado por un juego. Siempre reembolsa a cartera.
     * @param idCompra
     * @throws ValidationException
     */
    void solicitarReembolso(Long idCompra) throws ValidationException;

    /**
     * Crea la factura de la compra.
     * @param idCompra
     * @return muestra un objeto DTO.
     * @throws ValidationException
     */
    FacturaDto generarFactura(Long idCompra) throws ValidationException;
}
