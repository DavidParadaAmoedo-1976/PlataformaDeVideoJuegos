package org.davidparada.controlador.util;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.entidad.BibliotecaEntidad;
import org.davidparada.modelo.entidad.CompraEntidad;
import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.enums.TipoErrorEnum;
import org.davidparada.modelo.formulario.validacion.ErrorModel;
import org.davidparada.repositorio.interfaces.IBibliotecaRepo;
import org.davidparada.repositorio.interfaces.ICompraRepo;
import org.davidparada.repositorio.interfaces.IJuegoRepo;
import org.davidparada.repositorio.interfaces.IUsuarioRepo;

import java.util.List;

public class ObtenerEntidadesOptional {

    private static ICompraRepo compraRepo = null;
    private static IUsuarioRepo usuarioRepo = null;
    private static IJuegoRepo juegoRepo = null;
    private static IBibliotecaRepo bibliotecaRepo = null;

    public ObtenerEntidadesOptional(ICompraRepo compraRepo, IUsuarioRepo usuarioRepo, IJuegoRepo juegoRepo, IBibliotecaRepo bibliotecaRepo) {
        ObtenerEntidadesOptional.compraRepo = compraRepo;
        ObtenerEntidadesOptional.usuarioRepo = usuarioRepo;
        ObtenerEntidadesOptional.juegoRepo = juegoRepo;
        ObtenerEntidadesOptional.bibliotecaRepo = bibliotecaRepo;
    }


    public static CompraEntidad obtenerCompra(Long idCompra, List<ErrorModel> errores) throws ValidationException {

        return compraRepo.buscarPorId(idCompra)
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
}
