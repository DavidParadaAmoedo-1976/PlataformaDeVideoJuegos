package org.davidparada.modelo.formulario.validacion;

import org.davidparada.modelo.enums.TipoErrorEnum;

public record ErrorModel(String campo, TipoErrorEnum error) {
}
