package org.davidparada.controlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.JuegoDto;
import org.davidparada.modelo.dto.ResenaDto;
import org.davidparada.modelo.entidad.BibliotecaEntidad;
import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.entidad.ResenaEntidad;
import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.enums.EstadoPublicacionEnum;
import org.davidparada.modelo.enums.OrdenarResenaEnum;
import org.davidparada.modelo.enums.TipoErrorEnum;
import org.davidparada.modelo.formulario.ResenaForm;
import org.davidparada.modelo.formulario.validacion.ErrorModel;
import org.davidparada.modelo.formulario.validacion.ResenaFormValidador;
import org.davidparada.modelo.mapper.JuegoEntidadADtoMapper;
import org.davidparada.modelo.mapper.ResenaEntidadADtoMapper;
import org.davidparada.modelo.mapper.UsuarioEntidadADtoMapper;
import org.davidparada.repositorio.interfaces.IResenaRepo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.davidparada.controlador.util.ComprobarErrores.comprobarListaErrores;
import static org.davidparada.controlador.util.ObtenerEntidadesOptional.*;

public class ResenaControlador {

    private final IResenaRepo resenaRepo;

    public ResenaControlador(IResenaRepo reseniaRepo) {
        this.resenaRepo = reseniaRepo;
    }

    // Escribir reseña
    public ResenaDto escribirResena(
            Long idUsuario,
            Long idJuego,
            boolean recomendado,
            String texto
    ) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (idUsuario == null) {
            errores.add(new ErrorModel("idUsuario", TipoErrorEnum.OBLIGATORIO));
        }
        if (idJuego == null) {
            errores.add(new ErrorModel("idJuego", TipoErrorEnum.OBLIGATORIO));
        }
        if (texto == null || texto.isBlank()) {
            errores.add(new ErrorModel("textoResena", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);

        UsuarioEntidad usuario = obtenerUsuario(idUsuario, errores);
        JuegoEntidad juego = obtenerJuego(idJuego, errores);
        obtenerBiblioteca(idUsuario,idJuego, errores);

        List<ResenaEntidad> resenasEntidad = resenaRepo.buscarPorJuego(idJuego);

        boolean yaExiste = resenasEntidad.stream()
                .anyMatch(r -> r.getIdUsuario().equals(idUsuario));
        if (yaExiste) {
            errores.add(new ErrorModel("resena", TipoErrorEnum.DUPLICADO));
        }
        comprobarListaErrores(errores);

        ResenaForm form = new ResenaForm(
                idUsuario,
                idJuego,
                recomendado,
                texto,
                0.0,
                Instant.now(),
                null,
                EstadoPublicacionEnum.PUBLICADA
        );

        ResenaFormValidador.validarResena(form);

        ResenaEntidad resena = resenaRepo.crear(form);

        return ResenaEntidadADtoMapper.resenaEntidadADto(resena, usuario, juego);
    }

    // Eliminar reseña
    public boolean eliminarResena(Long idResena, Long idUsuario) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        if (idResena == null) {
            errores.add(new ErrorModel("idResena", TipoErrorEnum.OBLIGATORIO));
        }
        if (idUsuario == null) {
            errores.add(new ErrorModel("idUsuario", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);

        ResenaEntidad resenaEntidad = obtenerResenaPorIdYUsuario(idResena, idUsuario, errores);

        return resenaRepo.eliminar(resenaEntidad.getIdResena());
    }

    // Ver reseñas de un juego
    public List<ResenaDto> obtenerResenas(Long idJuego,
                                          boolean recomendado,
                                          OrdenarResenaEnum orden) throws ValidationException {

        List<ErrorModel> errores = new ArrayList<>();

        if (idJuego == null) {
            errores.add(new ErrorModel("idJuego", TipoErrorEnum.OBLIGATORIO));
        }
        if (orden == null) {
            errores.add(new ErrorModel("orden", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);

        JuegoEntidad juegoEntidad = obtenerJuego(idJuego, errores);
        JuegoDto juegoDto = JuegoEntidadADtoMapper.juegoEntidadADto(juegoEntidad);

        List<ResenaEntidad> resenasEntidad = resenaRepo.buscarPorJuego(idJuego);

        Comparator<ResenaEntidad> comparador;

        if (orden == OrdenarResenaEnum.RECIENTES) {
            comparador = (ResenaEntidad r1, ResenaEntidad r2) ->
                    r2.getFechaPublicacion().compareTo(r1.getFechaPublicacion());
        } else {
            comparador = (ResenaEntidad r1, ResenaEntidad r2) -> 0;
        }

        List<ResenaEntidad> resenasFiltradasYOrdenadas
                = resenasEntidad.stream()

                .filter(r -> r.getEstadoPublicacion() == EstadoPublicacionEnum.PUBLICADA)

                .filter(r -> r.isRecomendado() == recomendado)

                .sorted(comparador)

                .toList();

        List<ResenaDto> resultado = new ArrayList<>();

        for (ResenaEntidad r : resenasFiltradasYOrdenadas) {

            UsuarioEntidad usuarioEntidad = obtenerUsuario(r.getIdUsuario(), errores);

            resultado.add(new ResenaDto(
                    r.getIdResena(),
                    r.getIdUsuario(),
                    UsuarioEntidadADtoMapper.usuarioEntidadADto(usuarioEntidad),
                    r.getIdJuego(),
                    juegoDto,
                    r.isRecomendado(),
                    r.getTextoResena(),
                    r.getCantidadHorasJugadas(),
                    r.getFechaPublicacion(),
                    r.getFechaUltimaEdicion(),
                    r.getEstadoPublicacion()
            ));
        }
        return resultado;
    }

    // Ocultar reseña
    public ResenaDto ocultarResena(Long idResena, Long idUsuario) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        if (idResena == null) {
            errores.add(new ErrorModel("idResena", TipoErrorEnum.OBLIGATORIO));
        }
        if (idUsuario == null) {
            errores.add(new ErrorModel("idUsuario", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);

        ResenaEntidad resenaEntidad = obtenerResenaPorIdYUsuario(idResena, idUsuario, errores);

        ResenaForm nuevaResena = new ResenaForm(
                resenaEntidad.getIdUsuario(),
                resenaEntidad.getIdJuego(),
                resenaEntidad.isRecomendado(),
                resenaEntidad.getTextoResena(),
                resenaEntidad.getCantidadHorasJugadas(),
                resenaEntidad.getFechaPublicacion(),
                Instant.now(),
                EstadoPublicacionEnum.OCULTA);

        Optional<ResenaEntidad> resenaActualizada = resenaRepo.actualizar(idResena, nuevaResena);
        ResenaEntidad resenaGuardada = resenaActualizada.orElseThrow();

        UsuarioEntidad usuarioEntidad = obtenerUsuario(idUsuario, errores);
        JuegoEntidad juegoEntidad = obtenerJuego(resenaEntidad.getIdJuego(), errores);

        return ResenaEntidadADtoMapper.resenaEntidadADto(
                resenaGuardada,
                usuarioEntidad,
                juegoEntidad
        );
    }

    // Consultar estadisticas de una reseña
    public List<ResenaDto> consultarEstadisticasResenaPorJuego(Long idJuego) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        if (idJuego == null) {
            errores.add(new ErrorModel("idJuego", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
        JuegoEntidad juegoEntidad = obtenerJuego(idJuego, errores);

        List<ResenaEntidad> resenasEntidad = resenaRepo.buscarPorJuego(idJuego);
        List<ResenaDto> resenasDto = new ArrayList<>();

        for (ResenaEntidad r : resenasEntidad) {
            UsuarioEntidad usuarioEntidad = obtenerUsuario(r.getIdUsuario(), errores);

            resenasDto.add(new ResenaDto(
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
                    r.getEstadoPublicacion()
            ));
        }
        return resenasDto;
    }

    // Ver reseñas de un usuario
    public List<ResenaDto> obtenerResenasUsuario(Long idUsuario) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        if (idUsuario == null) {
            errores.add(new ErrorModel("idUsuario", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
        UsuarioEntidad usuarioEntidad = obtenerUsuario(idUsuario, errores);

        List<ResenaEntidad> resenasEntidad = resenaRepo.buscarPorUsuario(idUsuario);
        List<ResenaDto> resenasDto = new ArrayList<>();

        for (ResenaEntidad r : resenasEntidad) {
            JuegoEntidad juegoEntidad = obtenerJuego(r.getIdJuego(), errores);
            resenasDto.add(new ResenaDto(
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
                    r.getEstadoPublicacion()
            ));
        }
        return resenasDto;
    }
}


