package org.DavidParada.modelo.formulario.validacion;

import org.DavidParada.excepcion.ValidationException;
import org.DavidParada.modelo.enums.TipoErrorEnum;
import org.DavidParada.modelo.formulario.BibliotecaForm;
import org.DavidParada.repositorio.interfaces.IBibliotecaRepo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BibliotecaFormValidador {
    private static IBibliotecaRepo bibliotecaRepo;

    private BibliotecaFormValidador() {
    }

    public static void validarBiblioteca(BibliotecaForm form) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (form == null) {
            errores.add(new ErrorModel("formulario biblioteca", TipoErrorEnum.NO_ENCONTRADO));
        }

        // Usuario
        ValidacionesComunes.obligatorio("usuario", form.getIdUsuario(), errores);

        // Juego
        ValidacionesComunes.obligatorio("juego", form.getIdJuego(), errores);

        // Fecha de compra
        ValidacionesComunes.obligatorio("fechaAdquisicion", form.getFechaAdquisicion(), errores);

        // Tiempo de juego total
        ValidacionesComunes.valorNoNegativo("horasDeJuego", form.getHorasDeJuego(), errores);
        validarUnSoloDecimal("horasDeJuego", form.getHorasDeJuego(), errores);

        // Ultima fecha de juego

        // Estado de instalación
    }

    private static void validarUnSoloDecimal(String campo, Double valor, List<ErrorModel> errores) {
        if (valor == null) return;

        if (BigDecimal.valueOf(valor).scale() > 1) {
            errores.add(new ErrorModel(campo, TipoErrorEnum.FORMATO_INVALIDO));
        }
    }
}
