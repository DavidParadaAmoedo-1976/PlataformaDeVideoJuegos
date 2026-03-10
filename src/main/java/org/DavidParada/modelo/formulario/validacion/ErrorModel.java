package org.DavidParada.modelo.formulario.validacion;

import org.DavidParada.modelo.enums.TipoErrorEnum;

public record ErrorModel(String campo, TipoErrorEnum error) {
}
