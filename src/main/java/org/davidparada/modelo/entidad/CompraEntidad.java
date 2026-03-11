package org.davidparada.modelo.entidad;

import org.davidparada.modelo.enums.EstadoCompraEnum;
import org.davidparada.modelo.enums.MetodoPagoEnum;

import java.time.Instant;

public class CompraEntidad {
    private final Long idCompra;
    private final Long idUsuario;
    private final Long idJuego;
    private final Instant fechaCompra;
    private final MetodoPagoEnum metodoPago;
    private final Double precioBase;
    private final Integer descuento;
    private final EstadoCompraEnum estadoCompra;

    public CompraEntidad(Long idCompra,
                         Long idUsuario,
                         Long idJuego,
                         Instant fechaCompra,
                         MetodoPagoEnum metodoPago,
                         Double precioBase,
                         Integer descuento,
                         EstadoCompraEnum estadoCompra) {

        this.idCompra = idCompra;
        this.idUsuario = idUsuario;
        this.idJuego = idJuego;
        this.fechaCompra = fechaCompra;
        this.metodoPago = metodoPago;
        this.precioBase = precioBase;
        this.descuento = descuento;
        this.estadoCompra = estadoCompra;
    }

    public Long getIdCompra() {
        return idCompra;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public Long getIdJuego() {
        return idJuego;
    }

    public Instant getFechaCompra() {
        return fechaCompra;
    }

    public MetodoPagoEnum getMetodoPago() {
        return metodoPago;
    }

    public Double getPrecioBase() {
        return precioBase;
    }

    public Integer getDescuento() {
        return descuento;
    }

    public EstadoCompraEnum getEstadoCompra() {
        return estadoCompra;
    }
}
