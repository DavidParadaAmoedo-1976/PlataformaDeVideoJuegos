package org.DavidParada.controlador;

import org.DavidParada.excepcion.ValidationException;
import org.DavidParada.modelo.dto.*;
import org.DavidParada.modelo.entidad.*;
import org.DavidParada.modelo.enums.CriterioPopularidadEnum;
import org.DavidParada.modelo.enums.TipoErrorEnum;
import org.DavidParada.modelo.formulario.validacion.ErrorModel;
import org.DavidParada.modelo.mapper.JuegoEntidadADtoMapper;
import org.DavidParada.repositorio.interfaces.*;

import java.time.Instant;
import java.time.chrono.ChronoLocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    public ReporteVentasDto generarReporteVentas(Instant inicio, Instant fin, Long idJuego, String desarrollador) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (inicio == null || fin == null) {
            errores.add(new ErrorModel("fechas", TipoErrorEnum.OBLIGATORIO));
        } else if (fin.isBefore(inicio)) {
            errores.add(new ErrorModel("fechas", TipoErrorEnum.RANGO_INVALIDO));
        }
        comprobarListaErrores(errores);

        List<CompraEntidad> comprasFiltradas = compraRepo.listarTodos().stream()

                .filter(c -> {
                    assert inicio != null;
                    return c.getFechaCompra().isAfter(inicio);
                })
                .filter(c -> {
                    assert fin != null;
                    return c.getFechaCompra().isBefore(fin);
                })
                .filter(c -> idJuego == null || c.getIdJuego().equals(idJuego))
                .filter(c -> {
                    if (desarrollador == null) return true;
                    JuegoEntidad juego = juegoRepo.buscarPorId(c.getIdJuego());
                    return juego != null && juego.getDesarrollador().equalsIgnoreCase(desarrollador);
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
        int nuevosUsuarios = (int) usuarioRepo.listarTodos().stream()
                .filter(u -> u.getFechaRegistro().isAfter(inicio))
                .filter(u -> u.getFechaRegistro().isBefore(fin))
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

        switch (criterio) {

            case MAS_VENDIDOS -> resultado = juegosMasVendidos(limite);
            case MEJOR_VALORADOS -> resultado = juegosMejorValorados(limite);
            case MAS_JUGADOS -> resultado = juegosMasJugados(limite);
            default -> resultado = List.of();
        }
        ;
        return resultado;
    }

    private List<JuegosPopularesDto> juegosMasVendidos(Integer limite) {

        Map<Long, Double> ranking = new HashMap<>();

        for (CompraEntidad compra : compraRepo.listarTodos()) {

            Long idJuego = compra.getIdJuego();

            ranking.put(idJuego,
                    ranking.getOrDefault(idJuego, 0.0) + 1);
        }

        return resultadoConsulta(ranking, limite);
    }

    private List<JuegosPopularesDto> juegosMejorValorados(Integer limite) {

        Map<Long, List<ResenaEntidad>> agrupadas = new HashMap<>();

        for (ResenaEntidad resenia : resenaRepo.listarTodos()) {

            agrupadas
                    .computeIfAbsent(resenia.getIdJuego(), k -> new ArrayList<>())
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

    private List<JuegosPopularesDto> juegosMasJugados(Integer limite) {

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
    ) {

        List<Map.Entry<Long, Double>> listaOrdenada = ranking.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(limite)
                .toList();

        List<JuegosPopularesDto> resultado = new ArrayList<>();

        int posicion = 1;

        for (Map.Entry<Long, Double> entry : listaOrdenada) {

            JuegoEntidad juegoEntidad = juegoRepo.buscarPorId(entry.getKey());

            if (juegoEntidad != null) {

                JuegosPopularesDto dto = new JuegosPopularesDto(
                        posicion,
                        JuegoEntidadADtoMapper.juegoEntidadADto(juegoEntidad),
                        entry.getValue()
                );

                resultado.add(dto);
                posicion++;
            }
        }

        return resultado;
    }

    private void comprobarListaErrores(List<ErrorModel> errores) throws ValidationException {
        if (!errores.isEmpty()) {
            throw new ValidationException(errores);
        }
    }
}
