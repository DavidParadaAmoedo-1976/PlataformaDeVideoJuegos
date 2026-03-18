package org.davidparada.controlador.interfaceControlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.CompraDto;
import org.davidparada.modelo.dto.DetallesCompraDto;
import org.davidparada.modelo.dto.FacturaDto;
import org.davidparada.modelo.enums.MetodoPagoEnum;

import java.util.List;

public interface ICompraControlador {
    // Realizar compra
    CompraDto realizarCompra(
            Long idUsuario,
            Long idJuego,
            MetodoPagoEnum metodoPago
    ) throws ValidationException;

    // Procesar pago
    void procesarPago(Long idCompra, MetodoPagoEnum metodoPago) throws ValidationException;

    // Consultar historial de compras
    List<CompraDto> listarCompras(Long idUsuario) throws ValidationException;

    CompraDto consultarCompra(Long idCompra, Long idUsuario) throws ValidationException;

    // Consultar detalles de una compra
    DetallesCompraDto detallesDeUnaCompra(Long idCompra, Long idUsuario) throws ValidationException;

    // Solicitar reembolso
    void solicitarReembolso(Long idCompra) throws ValidationException;

    // Generar factura
    FacturaDto generarFactura(Long idCompra) throws ValidationException;
}
