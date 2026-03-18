package org.davidparada.controlador.util;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.entidad.*;
import org.davidparada.modelo.enums.TipoErrorEnum;
import org.davidparada.modelo.formulario.validacion.ErrorModel;
import org.davidparada.repositorio.interfaceRepositorio.*;

import java.util.List;

public class ObtenerEntidadesOptional {

    private static ICompraRepo compraRepo = null;
    private static IUsuarioRepo usuarioRepo = null;
    private static IJuegoRepo juegoRepo = null;
    private static IBibliotecaRepo bibliotecaRepo = null;
    private static IResenaRepo resenaRepo = null;

    public ObtenerEntidadesOptional(ICompraRepo compraRepo,
                                    IUsuarioRepo usuarioRepo,
                                    IJuegoRepo juegoRepo,
                                    IBibliotecaRepo bibliotecaRepo,
                                    IResenaRepo resenaRepo) {
        ObtenerEntidadesOptional.compraRepo = compraRepo;
        ObtenerEntidadesOptional.usuarioRepo = usuarioRepo;
        ObtenerEntidadesOptional.juegoRepo = juegoRepo;
        ObtenerEntidadesOptional.bibliotecaRepo = bibliotecaRepo;
        ObtenerEntidadesOptional.resenaRepo = resenaRepo;
    }

    public static CompraEntidad obtenerCompra(Long idCompra, List<ErrorModel> errores) throws ValidationException {
        return compraRepo.buscarPorId(idCompra)
                .orElseThrow(() -> {
                    errores.add(new ErrorModel("compra", TipoErrorEnum.NO_ENCONTRADO));
                    return new ValidationException(errores);
                });
    }

    public static CompraEntidad obtenerCompraUsuario(Long idCompra, Long idUsuario, List<ErrorModel> errores) throws ValidationException {
        return compraRepo.buscarPorCompraYUsuario(idCompra, idUsuario)
                .orElseThrow(() -> {
                    errores.add(new ErrorModel("compra", TipoErrorEnum.NO_ENCONTRADO));
                    return new ValidationException(errores);
                });
    }

    public static UsuarioEntidad obtenerUsuario(Long idUsuario, List<ErrorModel> errores) throws ValidationException {
        return usuarioRepo.buscarPorId(idUsuario)
                .orElseThrow(() -> {
                    errores.add(new ErrorModel("usuario", TipoErrorEnum.NO_ENCONTRADO));
                    return new ValidationException(errores);
                });
    }

    public static JuegoEntidad obtenerJuego(Long idJuego, List<ErrorModel> errores) throws ValidationException {
        return juegoRepo.buscarPorId(idJuego)
                .orElseThrow(() -> {
                    errores.add(new ErrorModel("juego", TipoErrorEnum.NO_ENCONTRADO));
                    return new ValidationException(errores);
                });
    }

    public static BibliotecaEntidad obtenerBiblioteca(Long idUsuario, Long idJuego, List<ErrorModel> errores) throws ValidationException {
        return bibliotecaRepo.buscarPorUsuarioYJuego(idUsuario, idJuego)
                .orElseThrow(() -> {
                    errores.add(new ErrorModel("biblioteca", TipoErrorEnum.NO_ENCONTRADO));
                    return new ValidationException(errores);
                });
    }

    public static ResenaEntidad obtenerResena(Long idResena, List<ErrorModel> errores) throws ValidationException {
        return resenaRepo.buscarPorId(idResena).orElseThrow(() -> {
            errores.add(new ErrorModel("resena", TipoErrorEnum.NO_ENCONTRADO));
            return new ValidationException(errores);
        });
    }

    public static ResenaEntidad obtenerResenaPorIdYUsuario(Long idResena, Long idUsuario, List<ErrorModel> errores) throws ValidationException {
        return resenaRepo.buscarPorIdYUsuario(idResena, idUsuario).orElseThrow(() -> {
            errores.add(new ErrorModel("resena", TipoErrorEnum.NO_ENCONTRADO));
            return new ValidationException(errores);
        });
    }
}
