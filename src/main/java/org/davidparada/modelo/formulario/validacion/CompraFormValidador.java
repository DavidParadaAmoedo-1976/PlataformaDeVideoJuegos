package org.davidparada.modelo.formulario.validacion;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.enums.EstadoCompraEnum;
import org.davidparada.modelo.enums.TipoErrorEnum;
import org.davidparada.modelo.formulario.CompraForm;

import java.util.ArrayList;
import java.util.List;

public class CompraFormValidador {

    private CompraFormValidador() {
    }

    public static void validarCompra(CompraForm form) throws ValidationException {

        List<ErrorModel> errores = new ArrayList<>();

        if (form == null) {
            errores.add(new ErrorModel("formulario Compra", TipoErrorEnum.NO_ENCONTRADO));
            throw new ValidationException(errores);
        }
        // Usuario

        ValidacionesComunes.obligatorio("usuario", form.getIdUsuario(), errores);

        // Juego

        ValidacionesComunes.obligatorio("juego", form.getIdJuego(), errores);

        // Fecha de compra

        ValidacionesComunes.obligatorio("fechaCompra", form.getFechaCompra(), errores);

        // Método de pago

        ValidacionesComunes.obligatorio("metodoPago", form.getMetodoPago(), errores);

        // Precio sin descuento

        ValidacionesComunes.obligatorio("precioBase", form.getPrecioBase(), errores);
        ValidacionesComunes.valorNoNegativo("precioBase", form.getPrecioBase(), errores);
        ValidacionesComunes.maxDosDecimales("precioBase", form.getPrecioBase(), errores);

        // Descuento

        ValidacionesComunes.valorFueraDeRango("descuento", form.getPrecioBase(), 0d, 100d, errores);

        // Estado

        if (form.getEstadoCompra() != null) {

            boolean estadoValido =
                    form.getEstadoCompra() == EstadoCompraEnum.PENDIENTE ||
                            form.getEstadoCompra() == EstadoCompraEnum.COMPLETADA ||
                            form.getEstadoCompra() == EstadoCompraEnum.CANCELADA ||
                            form.getEstadoCompra() == EstadoCompraEnum.REEMBOLSADA;

            if (!estadoValido) {
                errores.add(new ErrorModel("estadoCompra", TipoErrorEnum.RANGO_INVALIDO));
            }
        }

        if (!errores.isEmpty()) {
            throw new ValidationException(errores);
        }

    }

}