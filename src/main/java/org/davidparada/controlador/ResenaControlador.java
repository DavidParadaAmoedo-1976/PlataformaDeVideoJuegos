package org.davidparada.controlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.JuegoDto;
import org.davidparada.modelo.dto.ResenaDto;
import org.davidparada.modelo.dto.UsuarioDto;
import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.entidad.ResenaEntidad;
import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.enums.EstadoPublicacionEnum;
import org.davidparada.modelo.enums.TipoErrorEnum;
import org.davidparada.modelo.formulario.ResenaForm;
import org.davidparada.modelo.formulario.validacion.ErrorModel;
import org.davidparada.modelo.formulario.validacion.ResenaFormValidador;
import org.davidparada.modelo.mapper.JuegoEntidadADtoMapper;
import org.davidparada.modelo.mapper.ResenaEntidadADtoMapper;
import org.davidparada.modelo.mapper.UsuarioEntidadADtoMapper;
import org.davidparada.repositorio.interfaces.IJuegoRepo;
import org.davidparada.repositorio.interfaces.IResenaRepo;
import org.davidparada.repositorio.interfaces.IUsuarioRepo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ResenaControlador {

    private final IResenaRepo resenaRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IJuegoRepo juegoRepo;

    public ResenaControlador(IResenaRepo reseniaRepo, IUsuarioRepo usuarioRepo, IJuegoRepo juegoRepo){
        this.resenaRepo = reseniaRepo;
        this.usuarioRepo = usuarioRepo;
        this.juegoRepo = juegoRepo;
    }

    // Escribir reseña
    public ResenaDto escribirResena(ResenaForm form) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        ResenaFormValidador.validarResena(form);
        UsuarioEntidad usuarioEntidad = usuarioRepo.buscarPorId(form.getIdUsuario());
        if (usuarioEntidad == null) {
            errores.add(new ErrorModel("usuario", TipoErrorEnum.NO_ENCONTRADO));
        }

        JuegoEntidad juegoEntidad = juegoRepo.buscarPorId(form.getIdJuego());
        if (juegoEntidad == null) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);

        ResenaEntidad resenaEntidad = resenaRepo.crear(form);

        return ResenaEntidadADtoMapper.resenaEntidadADto(resenaEntidad, usuarioEntidad, juegoEntidad);
    }

    // Eliminar reseña
    public boolean eliminarResena(Long idResena) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        if (idResena == null) {
            errores.add(new ErrorModel("idResena", TipoErrorEnum.OBLIGATORIO));
        }
        ResenaEntidad resenaEntidad = resenaRepo.buscarPorId(idResena);
        if (resenaEntidad == null) {
            errores.add(new ErrorModel("reseña", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);
        return resenaRepo.eliminar(idResena);

    }

    // Ver reseñas de un juego

    public List<ResenaDto> obtenerResenas(Long idJuego) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        if (idJuego == null) {
            errores.add(new ErrorModel("idJuego", TipoErrorEnum.OBLIGATORIO));
        }

        JuegoEntidad juegoEntidad = juegoRepo.buscarPorId(idJuego);
        if (juegoEntidad == null) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);

        List<ResenaEntidad> resenasEntidad = resenaRepo.buscarPorJuego(idJuego);
        if (resenasEntidad == null) {
            errores.add(new ErrorModel("resenas", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);
        return resenasEntidad.stream()
                .map(r -> {
                    UsuarioEntidad usuarioEntidad = usuarioRepo.buscarPorId(r.getIdUsuario());
                    return new ResenaDto(
                            r.getIdResena(),
                            r.getIdUsuario(),
                            UsuarioEntidadADtoMapper.usuarioEntidadADto(usuarioEntidad),
                            r.getIdJuego(),
                            JuegoEntidadADtoMapper.juegoEntidadADto(juegoEntidad),
                            r.isRecomendado(),
                            r.getTextoResena(),
                            r.getCantidadHorasJugadas(),
                            r.getFechaPublicacion(),
                            r.getFechaUltimaEdicion(),
                            r.getEstadoPublicacion());

                })
                .toList();
    }
    // Ocultar reseña

    public ResenaDto ocultarResena(Long idResena) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        if (idResena == null) {
            errores.add(new ErrorModel("idResena", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
        ResenaEntidad resenaEntidad = resenaRepo.buscarPorId(idResena);
        if (resenaEntidad == null) {
            errores.add(new ErrorModel("resena", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);

        ResenaForm nuevaResena = new ResenaForm(
                resenaEntidad.getIdUsuario(),
                resenaEntidad.getIdJuego(),
                resenaEntidad.isRecomendado(),
                resenaEntidad.getTextoResena(),
                resenaEntidad.getCantidadHorasJugadas(),
                resenaEntidad.getFechaPublicacion(),
                Instant.now(),
                EstadoPublicacionEnum.OCULTA);

        ResenaEntidad resenaActualizada = resenaRepo.actualizar(idResena, nuevaResena);
        UsuarioEntidad usuarioEntidad = usuarioRepo.buscarPorId(resenaActualizada.getIdUsuario());
        JuegoEntidad juegoEntidad = juegoRepo.buscarPorId(resenaActualizada.getIdJuego());

        return ResenaEntidadADtoMapper.resenaEntidadADto(resenaActualizada, usuarioEntidad, juegoEntidad);
    }

    // Consultar estadisticas de una reseña

    public List<ResenaDto> consultarEstadisticasResenaPorJuego(Long idJuego) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        if (idJuego == null) {
            errores.add(new ErrorModel("idJuego", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
        JuegoEntidad juegoEntidad = juegoRepo.buscarPorId(idJuego);
        if (juegoEntidad == null) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.NO_ENCONTRADO));
        }

        comprobarListaErrores(errores);
        List<ResenaEntidad> resenaEntidad = resenaRepo.buscarPorJuego(idJuego);

        return resenaEntidad.stream()
                .map(r -> {
                    UsuarioDto usuarioDto = UsuarioEntidadADtoMapper.usuarioEntidadADto(usuarioRepo.buscarPorId(r.getIdUsuario()));
                    JuegoDto juegoDto = JuegoEntidadADtoMapper.juegoEntidadADto(juegoEntidad);
                    return new ResenaDto(
                            r.getIdResena(),
                            r.getIdUsuario(),
                            usuarioDto,
                            r.getIdJuego(),
                            juegoDto,
                            r.isRecomendado(),
                            r.getTextoResena(),
                            r.getCantidadHorasJugadas(),
                            r.getFechaPublicacion(),
                            r.getFechaUltimaEdicion(),
                            r.getEstadoPublicacion()
                    );
                })
                .toList();
    }


    // Ver reseñas de un usuario

    public List<ResenaDto> obtenerResenasUsuario(Long idUsuario) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        if (idUsuario == null) {
            errores.add(new ErrorModel("idUsuario", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
        UsuarioEntidad usuarioEntidad = usuarioRepo.buscarPorId(idUsuario);
        if (usuarioEntidad == null) {
            errores.add(new ErrorModel("usuario", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);

        List<ResenaEntidad> resenaEntidad = resenaRepo.buscarPorUsuario(idUsuario);
        return resenaEntidad.stream()
                .map(r -> {
                    UsuarioDto usuarioDto = UsuarioEntidadADtoMapper.usuarioEntidadADto(usuarioEntidad);
                    JuegoDto juegoDto = JuegoEntidadADtoMapper.juegoEntidadADto(juegoRepo.buscarPorId(r.getIdJuego()));
                    return new ResenaDto(
                            r.getIdResena(),
                            r.getIdUsuario(),
                            usuarioDto,
                            r.getIdJuego(),
                            juegoDto,
                            r.isRecomendado(),
                            r.getTextoResena(),
                            r.getCantidadHorasJugadas(),
                            r.getFechaPublicacion(),
                            r.getFechaUltimaEdicion(),
                            r.getEstadoPublicacion()
                    );
                })
                .toList();
    }


    private void comprobarListaErrores(List<ErrorModel> errores) throws ValidationException {
        if (!errores.isEmpty()) {
            throw new ValidationException(errores);
        }
    }
}

