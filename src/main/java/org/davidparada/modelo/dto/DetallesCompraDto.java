package org.davidparada.modelo.dto;

public record DetallesCompraDto(CompraDto compraDto,
                                JuegoDto juegoDto,
                                FacturaDto facturaDto) {
}
