package org.DavidParada.modelo.formulario.validacion;

import org.DavidParada.excepcion.ValidationException;
import org.DavidParada.modelo.enums.PaisEnum;
import org.DavidParada.modelo.enums.TipoErrorEnum;
import org.DavidParada.modelo.formulario.UsuarioForm;
import org.DavidParada.repositorio.interfaces.IUsuarioRepo;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UsuarioFormValidador {

    private static final Integer EDAD_MINIMA = 13;
    private static final Integer PASSWORD_MAX = 20;
    private static final Integer PASSWORD_MINIMA = 8;
    private static final Integer LONGITUD_AVATAR_MAX = 100;
    private static final Integer LONGITUD_EMAIL_USUARIO_MAX = 100;
    private static final Integer LONGITUD_NOMBRE_REAL_MINIMA = 2;
    private static final Integer LONGITUD_NOMBRE_REAL_MAXIMA = 50;
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[A-Za-z_-][A-Za-z0-9_-]{2,19}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^(?!\\.)[a-zA-Z0-9._%+-]+(?<!\\.)@[a-zA-Z0-9-]+(\\.[a-zA-Z]{2,})+$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    private static IUsuarioRepo usuarioRepo;

    private UsuarioFormValidador() {
    }


    public static void setUsuarioRepo(IUsuarioRepo repo) {
        usuarioRepo = repo;
    }

    public static void validarUsuario(UsuarioForm form) throws ValidationException {

        List<ErrorModel> errores = new ArrayList<>();

        if (form == null) {
            errores.add(new ErrorModel("formulario Usuario", TipoErrorEnum.OTRO));
            throw new ValidationException(errores);
        }

        // NombreUsuario
        ValidacionesComunes.obligatorio("nombreUsuario", form.getNombreUsuario(), errores);
        validarNombreUsuario(form.getNombreUsuario(), errores);
        validarNombreUsuarioUnico(form.getNombreUsuario(), errores);

        // Email
        ValidacionesComunes.obligatorio("email", form.getEmail(), errores);
        ValidacionesComunes.LongitudMaxima("email", form.getEmail(), LONGITUD_EMAIL_USUARIO_MAX, errores);
        validarFormatoEmail(form.getEmail(), errores);
        validarEmailUnico(form.getEmail(), errores);

        // Password
        ValidacionesComunes.obligatorio("password", form.getPassword(), errores);
        ValidacionesComunes.LongitudMinima("password", form.getPassword(), PASSWORD_MINIMA, errores);
        ValidacionesComunes.LongitudMaxima("password", form.getPassword(), PASSWORD_MAX, errores);
        validarFormatoPassword(form.getPassword(), errores);

        // Nombre real
        ValidacionesComunes.obligatorio("nombreReal", form.getNombreReal(), errores);
        ValidacionesComunes.LongitudMinima("nombreReal", form.getNombreReal(), LONGITUD_NOMBRE_REAL_MINIMA, errores);
        ValidacionesComunes.LongitudMaxima("nombreReal", form.getNombreReal(), LONGITUD_NOMBRE_REAL_MAXIMA, errores);

        // Pais
        validarPaisEnLista(form.getPais(), errores);

        // FechaNacimiento
        validarFechaNacimiento(form.getFechaNacimiento(), errores);

        // Avatar
        ValidacionesComunes.LongitudMaxima("avatar", form.getAvatar(), LONGITUD_AVATAR_MAX, errores);

        // Saldo
        ValidacionesComunes.valorNoNegativo("saldo", form.getSaldo(), errores);
        ValidacionesComunes.maxDosDecimales("saldo", form.getSaldo(), errores);


        if (!errores.isEmpty()) {
            throw new ValidationException(errores);
        }
    }

    private static void validarPaisEnLista(PaisEnum pais, List<ErrorModel> errores) {
        if (pais == null) {
            errores.add(new ErrorModel("pais", TipoErrorEnum.OBLIGATORIO));
        }

    }

    private static void validarNombreUsuario(String nombre, List<ErrorModel> errores) {
        if (nombre == null || nombre.isBlank()) {
            return;
        }
        if (!USERNAME_PATTERN.matcher(nombre).matches()) {
            errores.add(new ErrorModel("nombreUsuario", TipoErrorEnum.FORMATO_INVALIDO));
        }
    }

    private static void validarFormatoPassword(String password, List<ErrorModel> errores) {
        if (password != null && !password.isBlank()
                && !PASSWORD_PATTERN.matcher(password).matches()) {
            errores.add(new ErrorModel("password", TipoErrorEnum.FORMATO_INVALIDO));
        }
    }

    private static void validarFormatoEmail(String email, List<ErrorModel> errores) {
        if (email != null && !email.isBlank()
                && !EMAIL_PATTERN.matcher(email).matches()) {
            errores.add(new ErrorModel("email", TipoErrorEnum.FORMATO_INVALIDO));
        }
    }

    private static void validarFechaNacimiento(LocalDate fecha, List<ErrorModel> errores) {
        if (fecha == null) {
            errores.add(new ErrorModel("fechaNacimiento", TipoErrorEnum.OBLIGATORIO));
            return;
        }
        if (fecha.isAfter(LocalDate.now())) {
            errores.add(new ErrorModel("fechaNacimiento", TipoErrorEnum.VALOR_NEGATIVO));
        }

        Integer edad = Period.between(fecha, LocalDate.now()).getYears();
        if (edad < EDAD_MINIMA) {
            errores.add(new ErrorModel("fechaNacimiento", TipoErrorEnum.VALOR_EXCEDIDO));
        }
    }

    private static void validarEmailUnico(String email, List<ErrorModel> errores) {
        if (email != null && usuarioRepo != null &&
                usuarioRepo.buscarPorEmail(email) != null) {

            errores.add(new ErrorModel("email", TipoErrorEnum.DUPLICADO));
        }
    }

    private static void validarNombreUsuarioUnico(String nombre, List<ErrorModel> errores) {
        if (nombre != null && usuarioRepo != null &&
                usuarioRepo.buscarPorNombreUsuario(nombre) != null) {

            errores.add(new ErrorModel("nombreUsuario", TipoErrorEnum.DUPLICADO));
        }
    }

}
