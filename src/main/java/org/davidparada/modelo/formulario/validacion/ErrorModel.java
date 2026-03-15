package org.davidparada.modelo.formulario.validacion;

import org.davidparada.modelo.enums.TipoErrorEnum;

import java.util.List;

public record ErrorModel(String campo, TipoErrorEnum error) implements List<ErrorModel> {
}
