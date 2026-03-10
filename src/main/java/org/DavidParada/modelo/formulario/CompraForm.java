package org.DavidParada.modelo.formulario;

import org.DavidParada.modelo.enums.EstadoCompraEnum;
import org.DavidParada.modelo.enums.MetodoPagoEnum;

import java.time.Instant;

public class CompraForm {
    private  Long idUsuario;
    private  Long idJuego;
    private  Instant fechaCompra;
    private  MetodoPagoEnum metodoPago;
    private  Double precioBase;
    private  Double precioFinal;
    private  EstadoCompraEnum estadoCompra;

    public CompraForm() {
    }

    public CompraForm(Long idUsuario,
                      Long idJuego,
                      Instant fechaCompra,
                      MetodoPagoEnum metodoPago,
                      Double precioBase,
                      Double precioFinal,
                      EstadoCompraEnum estadoCompra) {

        this.idUsuario = idUsuario;
        this.idJuego = idJuego;
        this.fechaCompra = fechaCompra;
        this.metodoPago = metodoPago;
        this.precioBase = precioBase;
        this.precioFinal = precioFinal;
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

    public Double getPrecioFinal() {return precioFinal;}

    public EstadoCompraEnum getEstadoCompra() {
        return estadoCompra;
    }
}

