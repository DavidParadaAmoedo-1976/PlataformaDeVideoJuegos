package org.davidparada.modelo.formulario.validacion;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.enums.ClasificacionJuegoEnum;
import org.davidparada.modelo.enums.TipoErrorEnum;
import org.davidparada.modelo.formulario.JuegoForm;
import org.davidparada.repositorio.interfaces.IJuegoRepo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JuegoFormValidador {
    private static IJuegoRepo juegoRepo;

    private JuegoFormValidador() {
    }

    public static void validarJuego(JuegoForm form) throws ValidationException {

        List<ErrorModel> errores = new ArrayList<>();

        if (form == null) {
            errores.add(new ErrorModel("formulario Juego", TipoErrorEnum.NO_ENCONTRADO));
            throw new ValidationException(errores);
        }

        // Titulo
        ValidacionesComunes.obligatorio("titulo", form.getTitulo(), errores);
        ValidacionesComunes.longitudMinima("titulo", form.getTitulo(), 1, errores);
        ValidacionesComunes.longitudMaxima("titulo", form.getTitulo(), 100, errores);
        validarTituloUnico(form.getTitulo(), errores);

        // Descripcion
        ValidacionesComunes.longitudMaxima("descripcion", form.getDescripcion(), 2000, errores);

        // Desarrollador
        ValidacionesComunes.obligatorio("desarrollador", form.getDesarrollador(), errores);
        ValidacionesComunes.longitudMinima("desarrollador", form.getDesarrollador(), 2, errores);
        ValidacionesComunes.longitudMaxima("desarrollador", form.getDesarrollador(), 100, errores);

        // Fecha de Lanzamiento
        validarFechaLanzamiento(form.getFechaLanzamiento(), errores);

        // Precio base
        validarPrecioBase(form.getPrecioBase(), errores);
        ValidacionesComunes.valorNoNegativo("precioBase", form.getPrecioBase(), errores);
        ValidacionesComunes.valorFueraDeRango("precioBase", form.getPrecioBase(), 0.0, 999.99, errores);
        ValidacionesComunes.maxDosDecimales("precioBase", form.getPrecioBase(), errores);

        // Descuento
        ValidacionesComunes.valorFueraDeRango("descuento", form.getDescuento(), 0d, 100d, errores);

        // Clasificación por edad
        validarClasificacionPorEdad(form.getClasificacionPorEdad(), errores);

        // Idioma
        validarIdioma(form.getIdiomas(), errores);
    }


    private static void validarIdioma(String[] idiomas, List<ErrorModel> errores) {
        if (idiomas != null) {
            if (idiomas.length == 0) {
                errores.add(new ErrorModel("idiomas", TipoErrorEnum.RANGO_INVALIDO));
            }
            for (String idioma : idiomas) {
                if (idioma.length() > 200) {
                    errores.add(new ErrorModel("idiomas", TipoErrorEnum.LONGITUD_EXCEDIDA));
                }
            }
        }
    }

    private static void validarClasificacionPorEdad(ClasificacionJuegoEnum clasificacionPorEdad, List<ErrorModel> errores) {
        if (clasificacionPorEdad == null) {
            errores.add(new ErrorModel("clasificacionPorEdad", TipoErrorEnum.OBLIGATORIO));
        }
    }

    private static void validarPrecioBase(Double precioBase, List<ErrorModel> errores) {
        if (precioBase == null) {
            errores.add(new ErrorModel("precioBase", TipoErrorEnum.OBLIGATORIO));
        }
    }

    private static void validarFechaLanzamiento(LocalDate fechaLanzamiento, List<ErrorModel> errores) {
        if (fechaLanzamiento == null) {
            errores.add(new ErrorModel("fechaLanzamiento", TipoErrorEnum.OBLIGATORIO));
        }
    }

    private static void validarTituloUnico(String titulo, List<ErrorModel> errores) {

        if (titulo == null || juegoRepo == null) return;

        if (juegoRepo.existeTitulo(titulo)) {
            errores.add(new ErrorModel("titulo", TipoErrorEnum.DUPLICADO));
        }
    }


    public static void setJuegoRepo(IJuegoRepo repo) {
        juegoRepo = repo;
    }
}



