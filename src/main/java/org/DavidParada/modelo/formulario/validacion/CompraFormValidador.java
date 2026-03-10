package org.DavidParada.modelo.formulario.validacion;

import org.DavidParada.excepcion.ValidationException;
import org.DavidParada.modelo.enums.EstadoCompraEnum;
import org.DavidParada.modelo.enums.TipoErrorEnum;
import org.DavidParada.modelo.formulario.CompraForm;

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


        // Precio final

        ValidacionesComunes.obligatorio("precioFinal", form.getPrecioFinal(), errores);
        ValidacionesComunes.valorNoNegativo("precioFinal", form.getPrecioFinal(), errores);
        ValidacionesComunes.maxDosDecimales("precioFinal", form.getPrecioFinal(), errores);

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