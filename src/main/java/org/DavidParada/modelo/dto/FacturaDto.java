package org.DavidParada.modelo.dto;

import org.DavidParada.modelo.enums.MetodoPagoEnum;

import java.time.Instant;

public record FacturaDto(Long numeroFactura,
                         Long idCompra,
                         String nombreUsuario,
                         String email,
                         Instant fechaEmision,
                         Double importe,
                         Integer descuento,
                         MetodoPagoEnum metodoPago) {
}
