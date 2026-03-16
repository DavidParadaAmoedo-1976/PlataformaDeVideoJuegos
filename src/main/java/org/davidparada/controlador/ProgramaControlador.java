package org.davidparada.controlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.*;
import org.davidparada.modelo.entidad.*;
import org.davidparada.modelo.enums.CriterioPopularidadEnum;
import org.davidparada.modelo.enums.TipoErrorEnum;
import org.davidparada.modelo.formulario.validacion.ErrorModel;
import org.davidparada.modelo.mapper.JuegoEntidadADtoMapper;
import org.davidparada.repositorio.interfaces.*;

import java.time.Instant;
import java.util.*;

import static org.davidparada.controlador.util.ComprobarErrores.comprobarListaErrores;
import static org.davidparada.controlador.util.ObtenerEntidadesOptional.*;

public class ProgramaControlador {

    private final ICompraRepo compraRepo;
    private final IJuegoRepo juegoRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IBibliotecaRepo bibliotecaRepo;
    private final IResenaRepo resenaRepo;

    public ProgramaControlador(
            ICompraRepo compraRepo,
            IJuegoRepo juegoRepo,
            IUsuarioRepo usuarioRepo,
            IBibliotecaRepo bibliotecaRepo,
            IResenaRepo resenaRepo
    ) {
        this.compraRepo = compraRepo;
        this.juegoRepo = juegoRepo;
        this.usuarioRepo = usuarioRepo;
        this.bibliotecaRepo = bibliotecaRepo;
        this.resenaRepo = resenaRepo;
    }

    // Generar reportes de ventas

    public ReporteVentasDto generarReporteVentas(Instant inicio,
                                                 Instant fin,
                                                 Long idJuego,
                                                 String desarrollador) throws ValidationException {

        List<ErrorModel> errores = new ArrayList<>();

        if (inicio == null || fin == null) {
            errores.add(new ErrorModel("fechas", TipoErrorEnum.OBLIGATORIO));
        } else if (fin.isBefore(inicio)) {
            errores.add(new ErrorModel("fechas", TipoErrorEnum.RANGO_INVALIDO));
        }
        comprobarListaErrores(errores);

        Instant inicioSeguro = java.util.Objects.requireNonNull(inicio);
        Instant finSeguro = java.util.Objects.requireNonNull(fin);

        List<CompraEntidad> comprasFiltradas = compraRepo.listarTodos().stream()

                .filter(c -> c.getFechaCompra().isAfter(inicioSeguro))
                .filter(c -> c.getFechaCompra().isBefore(finSeguro))

                .filter(c -> idJuego == null || c.getIdJuego().equals(idJuego))

                .filter(c -> {
                    if (desarrollador == null) {
                        return true;
                    }

                    Optional<JuegoEntidad> juego = juegoRepo.buscarPorId(c.getIdJuego());

                    return juego
                            .map(j -> j.getDesarrollador().equalsIgnoreCase(desarrollador))
                            .orElse(false);
                })

                .toList();

        int totalVentas = comprasFiltradas.size();

        double ingresosTotales = comprasFiltradas.stream()
                .mapToDouble(c -> c.getPrecioBase() * (1 - c.getDescuento() / 100.0))
                .sum();

        return new ReporteVentasDto(inicio, fin, totalVentas, ingresosTotales);
    }

    // Generar reportes de usuarios

    public ReporteUsuariosDto generarReporteUsuarios(Instant inicio, Instant fin) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        if (inicio == null || fin == null) {
            errores.add(new ErrorModel("fechas", TipoErrorEnum.OBLIGATORIO));
        } else if (fin.isBefore(inicio)) {
            errores.add(new ErrorModel("fechas", TipoErrorEnum.RANGO_INVALIDO));
        }
        comprobarListaErrores(errores);

        Instant inicioSeguro = java.util.Objects.requireNonNull(inicio);
        Instant finSeguro = java.util.Objects.requireNonNull(fin);

        int nuevosUsuarios = (int) usuarioRepo.listarTodos().stream()
                .filter(u -> u.getFechaRegistro().isAfter(inicioSeguro))
                .filter(u -> u.getFechaRegistro().isBefore(finSeguro))
                .count();

        int usuariosActivos = (int) compraRepo.listarTodos().stream()
                .map(c -> c.getIdUsuario())
                .distinct()
                .count();

        return new ReporteUsuariosDto(inicio, fin, nuevosUsuarios, usuariosActivos);
    }

    // Consultar juegos mas populares

    public List<JuegosPopularesDto> consultarJuegosMasPopulares(CriterioPopularidadEnum criterio, Integer limite) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        List<JuegosPopularesDto> resultado;
        if (criterio == null) {
            errores.add(new ErrorModel("criterio", TipoErrorEnum.OBLIGATORIO));
        } else if (limite == null) {
            errores.add(new ErrorModel("limite", TipoErrorEnum.OBLIGATORIO));
        } else if (limite <= 0) {
            errores.add(new ErrorModel("limite", TipoErrorEnum.NO_PERMITIDO));
        }
        comprobarListaErrores(errores);
        Objects.requireNonNull(criterio);

        switch (criterio) {

            case MAS_VENDIDOS -> resultado = juegosMasVendidos(limite);
            case MEJOR_VALORADOS -> resultado = juegosMejorValorados(limite);
            case MAS_JUGADOS -> resultado = juegosMasJugados(limite);
            default -> resultado = List.of();
        }
        return resultado;
    }

    private List<JuegosPopularesDto> juegosMasVendidos(Integer limite) throws ValidationException {

        Map<Long, Double> ranking = new HashMap<>();

        for (CompraEntidad compra : compraRepo.listarTodos()) {

            Long idJuego = compra.getIdJuego();

            ranking.put(idJuego,
                    ranking.getOrDefault(idJuego, 0.0) + 1);
        }

        return resultadoConsulta(ranking, limite);
    }

    private List<JuegosPopularesDto> juegosMejorValorados(Integer limite) throws ValidationException {

        Map<Long, List<ResenaEntidad>> agrupadas = new HashMap<>();

        for (ResenaEntidad resenia : resenaRepo.listarTodos()) {

            agrupadas
                    .computeIfAbsent(resenia.getIdJuego(), ignored -> new ArrayList<>())
                    .add(resenia);
        }

        Map<Long, Double> ranking = new HashMap<>();

        for (Map.Entry<Long, List<ResenaEntidad>> entry : agrupadas.entrySet()) {

            double media = entry.getValue().stream()
                    .mapToInt(r -> r.isRecomendado() ? 1 : 0)
                    .average()
                    .orElse(0.0);

            ranking.put(entry.getKey(), media);
        }

        return resultadoConsulta(ranking, limite);
    }

    private List<JuegosPopularesDto> juegosMasJugados(Integer limite) throws ValidationException {

        Map<Long, Double> ranking = new HashMap<>();

        for (BibliotecaEntidad biblioteca : bibliotecaRepo.listarTodos()) {

            Long idJuego = biblioteca.getIdJuego();

            ranking.put(idJuego,
                    ranking.getOrDefault(idJuego, 0.0)
                            + biblioteca.getHorasDeJuego());
        }

        return resultadoConsulta(ranking, limite);
    }

    private List<JuegosPopularesDto> resultadoConsulta(
            Map<Long, Double> ranking,
            Integer limite
    ) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        List<Map.Entry<Long, Double>> listaOrdenada = ranking.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(limite)
                .toList();

        List<JuegosPopularesDto> resultado = new ArrayList<>();

        int posicion = 1;

        for (Map.Entry<Long, Double> entry : listaOrdenada) {

            JuegoEntidad juegoEntidad = obtenerJuego(entry.getKey(), errores);

            JuegosPopularesDto dto = new JuegosPopularesDto(
                    posicion,
                    JuegoEntidadADtoMapper.juegoEntidadADto(juegoEntidad),
                    entry.getValue()
            );
            resultado.add(dto);
            posicion++;

        }

        return resultado;
    }
}
