package org.davidparada.modelo.mapper;

import org.davidparada.modelo.entidad.CompraEntidad;
import org.davidparada.modelo.enums.EstadoCompraEnum;
import org.davidparada.modelo.formulario.CompraForm;

import java.time.Instant;

public class CompraFormularioAEntidadMapper {

    public static final int DESCUENTO_POR_DEFECTO = 0;

    private CompraFormularioAEntidadMapper() {
    }

    public static CompraEntidad crearCompraEntidad(Long idCompra, CompraForm form) {

        return new CompraEntidad(
                idCompra,
                form.getIdUsuario(),
                form.getIdJuego(),
                Instant.now(),
                form.getMetodoPago(),
                redondear(form.getPrecioBase()),
                DESCUENTO_POR_DEFECTO,
                EstadoCompraEnum.PENDIENTE
        );
    }

    public static CompraEntidad actualizarCompraEntidad(Long idCompra, CompraForm form) {

        double precioBase = redondear(form.getPrecioBase());

        return new CompraEntidad(
                idCompra,
                form.getIdUsuario(),
                form.getIdJuego(),
                form.getFechaCompra(),
                form.getMetodoPago(),
                precioBase,
                form.getDescuento(),
                form.getEstadoCompra()
        );
    }

    private static int calcularDescuento(double precioBase, double precioFinal) {

        if (precioBase == 0) return 0;

        double porcentaje = 100 * (1 - (precioFinal / precioBase));

        return (int) Math.round(porcentaje);
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
