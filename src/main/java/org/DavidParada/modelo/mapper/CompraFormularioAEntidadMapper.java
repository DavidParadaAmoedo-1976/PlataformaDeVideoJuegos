package org.DavidParada.modelo.mapper;

import org.DavidParada.modelo.entidad.CompraEntidad;
import org.DavidParada.modelo.enums.EstadoCompraEnum;
import org.DavidParada.modelo.formulario.CompraForm;

import java.time.Instant;

public class CompraFormularioAEntidadMapper {

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
                0,
                EstadoCompraEnum.PENDIENTE
        );
    }

    public static CompraEntidad actualizarCompraEntidad(Long idCompra, CompraForm form) {

        double precioBase = redondear(form.getPrecioBase());
        double precioFinal = redondear(form.getPrecioFinal());

        int descuento = calcularDescuento(precioBase, precioFinal);

        return new CompraEntidad(
                idCompra,
                form.getIdUsuario(),
                form.getIdJuego(),
                form.getFechaCompra(),
                form.getMetodoPago(),
                precioBase,
                descuento,
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
