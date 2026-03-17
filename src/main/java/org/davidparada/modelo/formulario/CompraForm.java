package org.davidparada.modelo.formulario;

import org.davidparada.modelo.enums.EstadoCompraEnum;
import org.davidparada.modelo.enums.MetodoPagoEnum;

import java.time.Instant;

public class CompraForm {
    private final Long idUsuario;
    private final Long idJuego;
    private final Instant fechaCompra;
    private final MetodoPagoEnum metodoPago;
    private final Double precioBase;
    private final int descuento;
    private final EstadoCompraEnum estadoCompra;

    public CompraForm(Long idUsuario,
                      Long idJuego,
                      Instant fechaCompra,
                      MetodoPagoEnum metodoPago,
                      Double precioBase,
                      int descuento,
                      EstadoCompraEnum estadoCompra) {

        this.idUsuario = idUsuario;
        this.idJuego = idJuego;
        this.fechaCompra = fechaCompra;
        this.metodoPago = metodoPago;
        this.precioBase = precioBase;
        this.descuento = descuento;
        this.estadoCompra = estadoCompra;
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

    public int getDescuento() {
        return descuento;
    }

    public EstadoCompraEnum getEstadoCompra() {
        return estadoCompra;
    }
}

