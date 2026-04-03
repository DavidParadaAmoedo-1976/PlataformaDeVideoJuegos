package org.davidparada.controlador;

import org.davidparada.controlador.interfaceControlador.IUsuarioControlador;
import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.UsuarioDto;
import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.enums.EstadoCuentaEnum;
import org.davidparada.modelo.enums.TipoErrorEnum;
import org.davidparada.modelo.formulario.UsuarioForm;
import org.davidparada.modelo.formulario.validacion.ErrorModel;
import org.davidparada.modelo.formulario.validacion.UsuarioFormValidador;
import org.davidparada.modelo.mapper.UsuarioEntidadADtoMapper;
import org.davidparada.repositorio.interfaceRepositorio.IUsuarioRepo;

import java.util.ArrayList;
import java.util.List;

import static org.davidparada.controlador.util.ComprobarErrores.comprobarListaErrores;
import static org.davidparada.controlador.util.ObtenerEntidadesOptional.*;

public class UsuarioControlador implements IUsuarioControlador {

    public static final double SALDO_MIN_A_ANADIR = 5.0;
    public static final double SALDO_MAX_A_ANADIR = 500.0;
    public static final int CERO = 0;
    private final IUsuarioRepo usuarioRepo;

    public UsuarioControlador(IUsuarioRepo usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public UsuarioDto registrarUsuario(UsuarioForm form) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        UsuarioFormValidador.validarUsuario(form);

        if (usuarioRepo.buscarPorEmail(form.getEmail()).isPresent()) {
            errores.add(new ErrorModel("email", TipoErrorEnum.DUPLICADO));
        }
        if (usuarioRepo.buscarPorNombreUsuario(form.getNombreUsuario()).isPresent()) {
            errores.add(new ErrorModel("nombre", TipoErrorEnum.DUPLICADO));
        }
        comprobarListaErrores(errores);

        UsuarioEntidad usuario = usuarioRepo.crear(form);
        return UsuarioEntidadADtoMapper.usuarioEntidadADto(usuario);
    }


    @Override
    public UsuarioDto consultarPerfil(Long idUsuario) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (idUsuario == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
        UsuarioEntidad usuario = obtenerUsuario(idUsuario, errores);

        return UsuarioEntidadADtoMapper.usuarioEntidadADto(usuario);
    }

    @Override
    public UsuarioDto consultarPerfil(String nombreUsuario) {

        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            return null;
        }

        return usuarioRepo.buscarPorNombreUsuario(nombreUsuario)
                .map(UsuarioEntidadADtoMapper::usuarioEntidadADto)
                .orElse(null);
    }

    @Override
    public UsuarioDto anadirSaldo(Long idUsuario, Double cantidad) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (idUsuario == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));
        }
        if (cantidad == null) {
            errores.add(new ErrorModel("saldo", TipoErrorEnum.OBLIGATORIO));
        } else {
            if (cantidad <= CERO)
                errores.add(new ErrorModel("saldo", TipoErrorEnum.VALOR_NEGATIVO));

            if (cantidad < SALDO_MIN_A_ANADIR || cantidad > SALDO_MAX_A_ANADIR)
                errores.add(new ErrorModel("saldo", TipoErrorEnum.RANGO_INVALIDO));
        }
        comprobarListaErrores(errores);
        java.util.Objects.requireNonNull(cantidad);

        UsuarioEntidad usuarioEntidad = obtenerUsuario(idUsuario, errores);

        if (usuarioEntidad.getEstadoCuenta() != EstadoCuentaEnum.ACTIVA) {
            errores.add(new ErrorModel("estadoCuenta", TipoErrorEnum.ESTADO_INCORRECTO));
        }
        comprobarListaErrores(errores);

        usuarioRepo.actualizar(usuarioEntidad.getIdUsuario(), new UsuarioForm(
                usuarioEntidad.getNombreUsuario(),
                usuarioEntidad.getEmail(),
                usuarioEntidad.getPassword(),
                usuarioEntidad.getNombreReal(),
                usuarioEntidad.getPais(),
                usuarioEntidad.getFechaNacimiento(),
                usuarioEntidad.getFechaRegistro(),
                usuarioEntidad.getAvatar(),
                usuarioEntidad.getSaldo() + cantidad,
                usuarioEntidad.getEstadoCuenta()));

        return UsuarioEntidadADtoMapper.usuarioEntidadADto(usuarioEntidad);
    }

    @Override
    public UsuarioDto consultarSaldo(Long idUsuario) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (idUsuario == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
        UsuarioEntidad usuario = obtenerUsuario(idUsuario, errores);

        return UsuarioEntidadADtoMapper.usuarioEntidadADto(usuario);
    }

//    /**
//     * Cambia el estado de la cuenta del usuario que pertenece al ID recibido,
//     * le pone el valor recibido como segundo parametro.
//     * @param idUsuario
//     * @param nuevoEstado
//     * @throws ValidationException
//     */
//    public void cambiarEstado(Long idUsuario,
//                                    EstadoCuentaEnum nuevoEstado)
//            throws ValidationException {
//
//        List<ErrorModel> errores = new ArrayList<>();
//
//        if (idUsuario == null) {
//            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));
//        }
//        if (nuevoEstado == null) {
//            errores.add(new ErrorModel("estadoCuenta", TipoErrorEnum.OBLIGATORIO));
//        }
//        if (!errores.isEmpty()) {
//            throw new ValidationException(errores);
//        }
//        UsuarioEntidad usuario = obtenerUsuario(idUsuario, errores);
//
//        usuarioRepo.actualizar(usuario.getIdUsuario(), new UsuarioForm(
//                usuario.getNombreUsuario(),
//                usuario.getEmail(),
//                usuario.getPassword(),
//                usuario.getNombreReal(),
//                usuario.getPais(),
//                usuario.getFechaNacimiento(),
//                usuario.getFechaRegistro(),
//                usuario.getAvatar(),
//                usuario.getSaldo(),
//                nuevoEstado
//        ));
//    }
//
//    /**
//     * Elimina el usuario al que pertenece el ID recibido.
//     * @param id
//     * @return Indica si la operación a tenido éxito.
//     * @throws ValidationException
//     */
//    public boolean eliminarUsuario(Long id) throws ValidationException {
//        List<ErrorModel> errores = new ArrayList<>();
//        if (id == null) {
//            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));
//        }
//        comprobarListaErrores(errores);
//
//        UsuarioEntidad usuario = obtenerUsuario(id, errores);
//        return usuarioRepo.eliminar(usuario.getIdUsuario());
//    }
}
