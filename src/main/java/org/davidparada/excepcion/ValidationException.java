package org.davidparada.excepcion;

import org.davidparada.modelo.formulario.validacion.ErrorModel;

import java.util.List;

public class ValidationException extends Exception {


    private final List<ErrorModel> errores;

    public ValidationException(List<ErrorModel> errores) {
        this.errores = errores;
    }

    public List<ErrorModel> getErrores() {
        return errores;
    }
}

