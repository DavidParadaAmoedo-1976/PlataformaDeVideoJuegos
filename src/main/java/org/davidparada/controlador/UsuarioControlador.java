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
    private final IUsuarioRepo usuarioRepo;

    public UsuarioControlador(IUsuarioRepo usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }


    /**
     * Crea un usuario nuevo a partir de un formulario.
     * @param form
     * @return Lo muestra en un ObjetoDTO.
     * @throws ValidationException
     */
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

    /**
     * Muestra el perfil de un usuario a partir de su ID que es único.
     * @param idUsuario
     * @return Lo muestra en un objetoDTO.
     * @throws ValidationException
     */
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

    /**
     * Muestra el perfil de un usuario a partir de su nombre de usuario que es único.
     * @param nombreUsuario
     * @return Lo muestra en un objetoDTO.
     */
    @Override
    public UsuarioDto consultarPerfil(String nombreUsuario) {

        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            return null;
        }

        return usuarioRepo.buscarPorNombreUsuario(nombreUsuario)
                .map(UsuarioEntidadADtoMapper::usuarioEntidadADto)
                .orElse(null);
    }

    /**
     * Añade la cantidad recivida por parametro al saldo del usuario perteneciente al ID recibido.
     * @param idUsuario
     * @param cantidad
     * @throws ValidationException
     */
    @Override
    public void anadirSaldo(Long idUsuario, Double cantidad) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (idUsuario == null)
            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));

        if (cantidad == null) {
            errores.add(new ErrorModel("saldo", TipoErrorEnum.OBLIGATORIO));
        } else {
            if (cantidad <= 0)
                errores.add(new ErrorModel("saldo", TipoErrorEnum.VALOR_NEGATIVO));

            if (cantidad < SALDO_MIN_A_ANADIR || cantidad > SALDO_MAX_A_ANADIR)
                errores.add(new ErrorModel("saldo", TipoErrorEnum.RANGO_INVALIDO));
        }
        comprobarListaErrores(errores);
        java.util.Objects.requireNonNull(cantidad);

        UsuarioEntidad usuario = obtenerUsuario(idUsuario, errores);

        if (usuario.getEstadoCuenta() != EstadoCuentaEnum.ACTIVA)
            errores.add(new ErrorModel("estadoCuenta", TipoErrorEnum.ESTADO_INCORRECTO));
        comprobarListaErrores(errores);
        usuarioRepo.actualizar(usuario.getIdUsuario(), new UsuarioForm(
                usuario.getNombreUsuario(),
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getNombreReal(),
                usuario.getPais(),
                usuario.getFechaNacimiento(),
                usuario.getFechaRegistro(),
                usuario.getAvatar(),
                usuario.getSaldo() + cantidad,
                usuario.getEstadoCuenta()));
    }

    /**
     * Devuelve el saldo en cartera del usuario al que pertenece el ID recibido.
     * @param idUsuario
     * @return saldo en formato double.
     * @throws ValidationException
     */
    @Override
    public Double consultarSaldo(Long idUsuario) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (idUsuario == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
        UsuarioEntidad usuario = obtenerUsuario(idUsuario, errores);
        return usuario.getSaldo();
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
//        if (idUsuario == null)
//            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));
//
//        if (nuevoEstado == null)
//            errores.add(new ErrorModel("estadoCuenta", TipoErrorEnum.OBLIGATORIO));
//
//        if (!errores.isEmpty())
//            throw new ValidationException(errores);
//
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
//        if (id == null)
//            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));
//        comprobarListaErrores(errores);
//
//        UsuarioEntidad usuario = obtenerUsuario(id, errores);
//        return usuarioRepo.eliminar(usuario.getIdUsuario());
//    }
}
