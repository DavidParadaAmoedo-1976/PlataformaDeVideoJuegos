package org.davidparada.controlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.UsuarioDto;
import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.enums.EstadoCuentaEnum;
import org.davidparada.modelo.enums.TipoErrorEnum;
import org.davidparada.modelo.formulario.UsuarioForm;
import org.davidparada.modelo.formulario.validacion.ErrorModel;
import org.davidparada.modelo.formulario.validacion.UsuarioFormValidador;
import org.davidparada.modelo.mapper.UsuarioEntidadADtoMapper;
import org.davidparada.repositorio.interfaces.IUsuarioRepo;

import java.util.ArrayList;
import java.util.List;

import static org.davidparada.controlador.util.ComprobarErrores.comprobarListaErrores;

public class UsuarioControlador {

    private final IUsuarioRepo usuarioRepo;

    public UsuarioControlador(IUsuarioRepo usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    //Registrar nuevo usuario

    public UsuarioDto registrarUsuario(UsuarioForm form)
            throws ValidationException {

        UsuarioFormValidador.validarUsuario(form);

        UsuarioEntidad usuario = usuarioRepo.crear(form);

        return UsuarioEntidadADtoMapper.usuarioEntidadADto(usuario);
    }

    // Consultar perfil

    public UsuarioDto consultarPerfil(Long id) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (id == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
        UsuarioEntidad usuario = usuarioRepo.buscarPorId(id);

        if (usuario == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);
        return UsuarioEntidadADtoMapper.usuarioEntidadADto(usuario);
    }

    // Anadir saldo a cartera

    public void anadirSaldo(Long id, Double cantidad) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (id == null)
            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));

        if (cantidad == null) {
            errores.add(new ErrorModel("saldo", TipoErrorEnum.OBLIGATORIO));
        } else {
            if (cantidad <= 0)
                errores.add(new ErrorModel("saldo", TipoErrorEnum.VALOR_NEGATIVO));

            if (cantidad < 5.0 || cantidad > 500.0)
                errores.add(new ErrorModel("saldo", TipoErrorEnum.RANGO_INVALIDO));
        }
        comprobarListaErrores(errores);
        UsuarioEntidad usuario = usuarioRepo.buscarPorId(id);

        if (usuario == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);
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

    // Consultar saldo

    public Double consultarSaldo(Long id) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (id == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
        UsuarioEntidad usuario = usuarioRepo.buscarPorId(id);

        if (usuario == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);
        return usuario.getSaldo();
    }

//
//    // Cambiar estado
//
//    public void cambiarEstado(Long id,
//                                    EstadoCuentaEnum nuevoEstado)
//            throws ValidationException {
//
//        List<ErrorModel> errores = new ArrayList<>();
//
//        if (id == null)
//            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));
//
//        if (nuevoEstado == null)
//            errores.add(new ErrorModel("estadoCuenta", TipoErrorEnum.OBLIGATORIO));
//
//        if (!errores.isEmpty())
//            throw new ValidationException(errores);
//
//        UsuarioEntidad usuario = usuarioRepo.buscarPorId(id);
//
//        if (usuario == null)
//            throw new ValidationException(List.of(
//                    new ErrorModel("id", TipoErrorEnum.NO_ENCONTRADO)));
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
//    // Eliminar usuario
//
//    public boolean eliminarUsuario(Long id) {
//        return usuarioRepo.eliminar(id);
//    }
}
