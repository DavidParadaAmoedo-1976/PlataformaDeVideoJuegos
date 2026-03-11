package org.davidparada.modelo.formulario.validacion;

import org.davidparada.modelo.enums.TipoErrorEnum;
import org.davidparada.modelo.formulario.ResenaForm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ResenaFormValidador {
    private ResenaFormValidador() {
    }

    public static void validarResena(ResenaForm form){
        List<ErrorModel> errores = new ArrayList<>();

        if (form == null) {
            errores.add(new ErrorModel("formulario reseña", TipoErrorEnum.NO_ENCONTRADO));
        }

        // Usuario
        ValidacionesComunes.obligatorio("usuario", form.getIdUsuario(), errores);

        // Juego
        ValidacionesComunes.obligatorio("juego", form.getIdJuego(), errores);

        // Recomendado
        ValidacionesComunes.obligatorio("recomendado", form.isRecomendado(), errores);

        // Texto de la reseña
        ValidacionesComunes.obligatorio("textResena", form.getTextoResena(), errores);
        ValidacionesComunes.valorFueraDeRango("textoResena", form.getTextoResena().length(), 50d, 8000d, errores);

        // Horas jugadas al momento de la reseña
        ValidacionesComunes.valorNoNegativo("cantidadHorasJugadas", form.getCantidadHorasJugadas(), errores);
        validarMaxUnDecimal("cantidadHorasJugadas", form.getCantidadHorasJugadas(), errores);

        // Fecha de Publicación

        // Fecha última edición

        // Estado


    }

    public static void validarMaxUnDecimal(String campo, Double valor, List<ErrorModel> errores) {
        if (valor == null) return;

        if (BigDecimal.valueOf(valor).scale() > 1) {
            errores.add(new ErrorModel(campo, TipoErrorEnum.FORMATO_INVALIDO));
        }
    }
}
