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
    public static final int LONGITUD_MIN_TITULO = 1;
    public static final int LONGITUD_MAX_TITULO = 100;
    public static final int LONGITUD_MAX_DESCRIPCION = 2000;
    public static final int LONGITUD_MIN_NOMBRE_DESARROLLADOR = 2;
    public static final int LONGITUD_MAX_NOMBRE_DESARROLLADOR = 100;
    public static final double VALOR_MIN_PRECIO = 0.0;
    public static final double VALOR_MAX_PRECIO = 999.99;
    public static final double DESCUENTO_MIN = 0d;
    public static final double DESCUENTO_MAX = 100d;
    public static final int LONGITUD_MIN_IDIOMA = 0;
    public static final int LONGITUD_MAX_IDIOMA = 200;
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
        ValidacionesComunes.longitudMinima("titulo", form.getTitulo(), LONGITUD_MIN_TITULO, errores);
        ValidacionesComunes.longitudMaxima("titulo", form.getTitulo(), LONGITUD_MAX_TITULO, errores);

        // Descripcion
        ValidacionesComunes.longitudMaxima("descripcion", form.getDescripcion(), LONGITUD_MAX_DESCRIPCION, errores);

        // Desarrollador
        ValidacionesComunes.obligatorio("desarrollador", form.getDesarrollador(), errores);
        ValidacionesComunes.longitudMinima("desarrollador", form.getDesarrollador(), LONGITUD_MIN_NOMBRE_DESARROLLADOR, errores);
        ValidacionesComunes.longitudMaxima("desarrollador", form.getDesarrollador(), LONGITUD_MAX_NOMBRE_DESARROLLADOR, errores);

        // Fecha de Lanzamiento
        validarFechaLanzamiento(form.getFechaLanzamiento(), errores);


        // Precio base
        validarPrecioBase(form.getPrecioBase(), errores);
        ValidacionesComunes.valorNoNegativo("precioBase", form.getPrecioBase(), errores);
        ValidacionesComunes.valorFueraDeRango("precioBase", form.getPrecioBase(), VALOR_MIN_PRECIO, VALOR_MAX_PRECIO, errores);
        ValidacionesComunes.maxDosDecimales("precioBase", form.getPrecioBase(), errores);

        // Descuento
        ValidacionesComunes.valorFueraDeRango("descuento", form.getDescuento(), DESCUENTO_MIN, DESCUENTO_MAX, errores);

        // Clasificación por edad
        validarClasificacionPorEdad(form.getClasificacionPorEdad(), errores);

        // Idioma
        validarIdioma(form.getIdiomas(), errores);

        if (!errores.isEmpty()) {
            throw new ValidationException(errores);
        }
    }


    private static void validarIdioma(String[] idiomas, List<ErrorModel> errores) {
        if (idiomas != null) {
            if (idiomas.length == LONGITUD_MIN_IDIOMA) {
                errores.add(new ErrorModel("idiomas", TipoErrorEnum.RANGO_INVALIDO));
            }
            for (String idioma : idiomas) {
                if (idioma.length() > LONGITUD_MAX_IDIOMA) {
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

    public static void setJuegoRepo(IJuegoRepo repo) {
        juegoRepo = repo;
    }
}



