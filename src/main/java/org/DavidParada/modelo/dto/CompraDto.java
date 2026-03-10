package org.DavidParada.modelo.dto;

import org.DavidParada.modelo.enums.EstadoCompraEnum;
import org.DavidParada.modelo.enums.MetodoPagoEnum;

import java.time.Instant;
import java.time.LocalDate;

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

