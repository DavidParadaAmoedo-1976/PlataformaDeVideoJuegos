package org.davidparada.controlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.EstadisticasResenasJuegoDto;
import org.davidparada.modelo.dto.JuegoDto;
import org.davidparada.modelo.dto.ResenaDto;
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

    public static final double CANTIDAD_HORAS_JUGADAS_POR_DEFECTO = 0.0;
    public static final int VALOR_MIN_POS_EN_ESTADISTICA = 60;
    public static final int VALOR_MAX_NEG_EN_ESTADISTICA = 40;
    public static final double CALCULO_PORCENTAJE = 100.0;
    private final IResenaRepo resenaRepo;

    public ResenaControlador(IResenaRepo reseniaRepo) {
        this.resenaRepo = reseniaRepo;
    }

    /**
     * Crea una reseña a partir de los datos recibidos por parametros.
     * @param idUsuario
     * @param idJuego
     * @param recomendado
     * @param texto
     * @return Lo muestra en un objeto DTO.
     * @throws ValidationException
     */
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
        obtenerBiblioteca(idUsuario, idJuego, errores);

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
                CANTIDAD_HORAS_JUGADAS_POR_DEFECTO,
                Instant.now(),
                null,
                EstadoPublicacionEnum.PUBLICADA
        );

        ResenaFormValidador.validarResena(form);

        ResenaEntidad resena = resenaRepo.crear(form);

        return ResenaEntidadADtoMapper.resenaEntidadADto(resena, usuario, juego);
    }

    /**
     * Elimina una reseña indicada por el ID recibido y que pertenecea un usuario del que se recibe su ID.
     * @param idResena
     * @param idUsuario
     * @return Indica si la operación a tenido éxito.
     * @throws ValidationException
     */
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

    /**
     * Obtiene las reseñas de un juego recibido su ID, muestra los juegos recomendados o no segun el segundo parametro,
     * y los muestra ordenados segun la clasificacion seleccionada en el tercer parametro.
     * @param idJuego
     * @param recomendado
     * @param orden
     * @return Lista de objetos DTO
     * @throws ValidationException
     */
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

        switch (orden) {
            case RECIENTES:
                comparador = (r1, r2) ->
                        r2.getFechaPublicacion().compareTo(r1.getFechaPublicacion());
                break;

            case HORAS_JUGADAS:
                comparador = (r1, r2) ->
                        r2.getCantidadHorasJugadas().compareTo(r1.getCantidadHorasJugadas());
                break;

            case ACTUALIZADAS:
                comparador = (r1, r2) ->
                        r2.getFechaUltimaEdicion().compareTo(r1.getFechaUltimaEdicion());
                break;

            default:
                comparador = Comparator.comparing(ResenaEntidad::getIdResena);
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

    /**
     * Oculta una reseña recibido su ID y que pertenece a un usuario que también recibimos su ID.
     * @param idResena
     * @param idUsuario
     * @return Lo muestra en un objeto DTO.
     * @throws ValidationException
     */
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

    /**
     * Muestra las estadisticas de las reseñas de un juego del cual recibimos su ID.
     * @param idJuego
     * @return Muestra un objeto DTO.
     * @throws ValidationException
     */
    public EstadisticasResenasJuegoDto consultarEstadisticasResenaPorJuego(Long idJuego) throws ValidationException {

        List<ErrorModel> errores = new ArrayList<>();

        if (idJuego == null) {
            errores.add(new ErrorModel("idJuego", TipoErrorEnum.OBLIGATORIO));
        }

        comprobarListaErrores(errores);

        // Validar que el juego existe
        obtenerJuego(idJuego, errores);

        List<ResenaEntidad> resenas = resenaRepo.buscarPorJuego(idJuego);

        int total = resenas.size();

        if (total == 0) {
            return new EstadisticasResenasJuegoDto(0, 0, 0, 0, "NEUTRA");
        }

        int positivas = 0;
        double sumaHoras = 0;

        for (ResenaEntidad r : resenas) {
            if (r.isRecomendado()) {
                positivas++;
            }
            sumaHoras += r.getCantidadHorasJugadas();
        }

        int negativas = total - positivas;

        double porcentajePositivas = (positivas * CALCULO_PORCENTAJE) / total;
        double porcentajeNegativas = (negativas * CALCULO_PORCENTAJE) / total;
        double promedioHoras = sumaHoras / total;

        // Ejemplo simple de tendencia (puedes mejorarla)
        String tendencia;
        if (porcentajePositivas > VALOR_MIN_POS_EN_ESTADISTICA) {
            tendencia = "POSITIVA";
        } else if (porcentajePositivas < VALOR_MAX_NEG_EN_ESTADISTICA) {
            tendencia = "NEGATIVA";
        } else {
            tendencia = "NEUTRA";
        }

        return new EstadisticasResenasJuegoDto(
                total,
                porcentajePositivas,
                porcentajeNegativas,
                promedioHoras,
                tendencia
        );
    }

    /**
     *
     * @param idUsuario
     * @return Lista de objetos DTO.
     * @throws ValidationException
     */
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