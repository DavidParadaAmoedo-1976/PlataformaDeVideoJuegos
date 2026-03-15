package org.davidparada.controlador.util;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.enums.TipoErrorEnum;
import org.davidparada.modelo.formulario.validacion.ErrorModel;

import java.util.List;

import static org.davidparada.controlador.util.ComprobarErrores.comprobarListaErrores;

public class ComprobarId {
    public static void comprobarIdUsuario(Long idUsuario, List<ErrorModel> errores) throws ValidationException {

        if (idUsuario == null) {
            errores.add(new ErrorModel("usuario", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
    }

    public static void comprobarIdJuego(Long idJuego, List<ErrorModel> errores) throws ValidationException {

        if (idJuego == null) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
    }

    public static void comprobarIdCompra(Long idCompra, List<ErrorModel> errores) throws ValidationException {
        if (idCompra == null) {
            errores.add(new ErrorModel("compra", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
    }
}
