package org.davidparada.modelo.formulario.validacion;

import org.davidparada.modelo.enums.TipoErrorEnum;

import java.math.BigDecimal;
import java.util.List;

public class ValidacionesComunes {

    public static final int MAX_DECIMALES_DOS = 2;

    private ValidacionesComunes() {
    }

    public static void obligatorio(String campo, Object valor, List<ErrorModel> errores) {
        if (valor == null) {
            errores.add(new ErrorModel(campo, TipoErrorEnum.OBLIGATORIO));
            return;
        }
        if (valor instanceof String string && string.isBlank()) {
            errores.add(new ErrorModel(campo, TipoErrorEnum.OBLIGATORIO));
        }
    }


    public static void longitudMaxima(String campo, String valor, Integer max, List<ErrorModel> errores) {
        if (valor != null && valor.length() > max) {
            errores.add(new ErrorModel(campo, TipoErrorEnum.LONGITUD_EXCEDIDA));
        }
    }

    public static void longitudMinima(String campo, String valor, Integer min, List<ErrorModel> errores) {
        if (valor != null && valor.length() < min) {
            errores.add(new ErrorModel(campo, TipoErrorEnum.RANGO_INVALIDO));
        }
    }

    public static void valorNoNegativo(String campo, Number valor, List<ErrorModel> errores) {
        if (valor == null) {
            return; // la obligatoriedad se valida aparte
        }

        if (valor.doubleValue() < 0) {
            errores.add(new ErrorModel(campo, TipoErrorEnum.VALOR_NEGATIVO));
        }
    }


    public static void valorFueraDeRango(String campo, Number valor, Double min, Double max, List<ErrorModel> errores) {
        if (valor == null) {
            return;
        }
        double v = valor.doubleValue();
        if (v < min || v > max) {
            errores.add(new ErrorModel(campo, TipoErrorEnum.RANGO_INVALIDO));
        }
    }

    public static void maxDosDecimales(String campo, Double valor, List<ErrorModel> errores) {
        if (valor == null) return;

        if (BigDecimal.valueOf(valor).scale() > MAX_DECIMALES_DOS) {
            errores.add(new ErrorModel(campo, TipoErrorEnum.FORMATO_INVALIDO));
        }
    }
}

