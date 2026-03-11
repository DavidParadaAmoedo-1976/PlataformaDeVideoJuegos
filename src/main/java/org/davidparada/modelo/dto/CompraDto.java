package org.davidparada.modelo.dto;

import org.davidparada.modelo.enums.EstadoCompraEnum;
import org.davidparada.modelo.enums.MetodoPagoEnum;

import java.time.Instant;

public record CompraDto(Long idCompra,
                        Long idUsuario,
                        UsuarioDto usuarioDto,
                        Long idJuego,
                        JuegoDto juegoDto,
                        Instant fechaCompra,
                        MetodoPagoEnum metodoPago,
                        Double precioBase,
                        Integer descuento,
                        EstadoCompraEnum estadoCompra) {
}

