package org.davidparada.controlador;

import org.davidparada.controlador.interfaceControlador.IBibliotecaControlador;
import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.BibliotecaDto;
import org.davidparada.modelo.dto.EstadisticasBibliotecaDto;
import org.davidparada.modelo.entidad.BibliotecaEntidad;
import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.enums.OrdenarJuegosBibliotecaEnum;
import org.davidparada.modelo.enums.TipoErrorEnum;
import org.davidparada.modelo.formulario.BibliotecaForm;
import org.davidparada.modelo.formulario.validacion.ErrorModel;
import org.davidparada.modelo.mapper.BibliotecaEntidadADtoMapper;
import org.davidparada.modelo.mapper.JuegoEntidadADtoMapper;
import org.davidparada.modelo.mapper.UsuarioEntidadADtoMapper;
import org.davidparada.repositorio.interfaceRepositorio.IBibliotecaRepo;
import org.davidparada.repositorio.interfaceRepositorio.IJuegoRepo;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.davidparada.controlador.util.ComprobarErrores.comprobarListaErrores;
import static org.davidparada.controlador.util.ObtenerEntidadesOptional.*;

public class BibliotecaControlador implements IBibliotecaControlador {

    public static final double HORAS_DE_JUEGO_POR_DEFECTO = 0.0;
    public static final double INICIO_VARIABLE_DOUBLE = 0.0;
    public static final int INICIO_VARIABLE_NEG = -1;
    public static final int CERO = 0;
    private final IBibliotecaRepo bibliotecaRepo;
    private final IJuegoRepo juegoRepo;

    public BibliotecaControlador(IBibliotecaRepo bibliotecaRepo,
                                 IJuegoRepo juegoRepo) {

        this.bibliotecaRepo = bibliotecaRepo;
        this.juegoRepo = juegoRepo;
    }

    // Ver Biblioteca personal

    @Override
    public List<BibliotecaDto> verBiblioteca(Long idUsuario, OrdenarJuegosBibliotecaEnum orden) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        comprobarIdUsuario(idUsuario, errores);

        List<BibliotecaEntidad> juegosEntidad = bibliotecaRepo.buscarPorUsuario(idUsuario); // Guarda todos los juegos de la biblioteca del usuario en una lista.

        // Mapea la lista de Entidad a un DTO para poder mostrar los datos del juego

        UsuarioEntidad usuario = obtenerUsuario(idUsuario, errores);

        List<BibliotecaDto> juegos = juegosEntidad.stream()
                .map(b -> {
                    JuegoEntidad juego = juegoRepo.buscarPorId(b.getIdJuego()).orElseThrow();

                    return new BibliotecaDto(
                            b.getIdBiblioteca(),
                            b.getIdUsuario(),
                            UsuarioEntidadADtoMapper.usuarioEntidadADto(usuario),
                            b.getIdJuego(),
                            JuegoEntidadADtoMapper.juegoEntidadADto(juego),
                            b.getFechaAdquisicion(),
                            b.getHorasDeJuego(),
                            b.getUltimaFechaDeJuego(),
                            b.isEstadoInstalacion()
                    );
                })
                .toList();

        // Ahora que tengo la lista de juegos en Dto para mostrar toca ver como la ordeno.

        if (orden != null) {        // Si la variable orden no es nula pasa al switch para ver como tiene que ordenar.

            switch (orden) {
                // Orden alfabetico
                case ALFABETICO -> juegos = juegos.stream()
                        .sorted(Comparator.comparing(b -> b.juegoDto().titulo()))
                        .toList();

                // Ordena por tiempo de Juego
                case TIEMPO_DE_JUEGO -> juegos = juegos.stream()
                        .sorted(Comparator.comparing((BibliotecaDto b) -> b.horasDeJuego()).reversed())
                        .toList();

                // Ordena por la última sesión
                case ULTIMA_SESION -> juegos = juegos.stream()
                        .sorted(Comparator.comparing((BibliotecaDto j) -> j.ultimaFechaDeJuego()).reversed())
                        .toList();

                // Ordena por fecha de adquisición
                case FECHA_DE_ADQUISICION -> juegos = juegos.stream()
                        .sorted(Comparator.comparing((BibliotecaDto b) -> b.fechaAdquisicion()).reversed())
                        .toList();

                default -> throw new IllegalArgumentException("No se encontro el orden");
            }
        }
        return juegos;
    }

    // Añadir juego a biblioteca

    @Override
    public BibliotecaDto anadirJuego(Long idUsuario, Long idJuego) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        comprobarIdUsuario(idUsuario, errores);
        comprobarIdJuego(idJuego, errores);

        List<BibliotecaEntidad> bibliotecasEntidad = bibliotecaRepo.buscarPorUsuario(idUsuario);

        boolean yaTieneJuego = bibliotecasEntidad.stream().anyMatch(b -> b.getIdJuego().equals(idJuego));

        if (yaTieneJuego) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.DUPLICADO));
            throw new ValidationException(errores);
        }
        BibliotecaForm nuevoJuego = new BibliotecaForm(
                idUsuario,
                idJuego,
                Instant.now(),
                HORAS_DE_JUEGO_POR_DEFECTO,
                null,
                false
        );

        BibliotecaEntidad nuevoJuegoEntidad = bibliotecaRepo.crear(nuevoJuego);
        UsuarioEntidad usuarioEntidad = obtenerUsuario(idUsuario, errores);
        JuegoEntidad juegoEntidad = obtenerJuego(idJuego, errores);

        return BibliotecaEntidadADtoMapper.bibliotecaEntidadADto(nuevoJuegoEntidad, usuarioEntidad, juegoEntidad);
    }

    // Eliminar juego de biblioteca

    @Override
    public void eliminarJuego(Long idUsuario, Long idJuego) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        comprobarIdUsuario(idUsuario, errores);
        comprobarIdJuego(idJuego, errores);

        BibliotecaEntidad bibliotecaEntidad = obtenerBiblioteca(idUsuario, idJuego, errores);

        bibliotecaRepo.eliminar(bibliotecaEntidad.getIdBiblioteca());
    }

    // Actualizar tiempo de juego

    @Override
    public void anadirTiempoDeJuego(Long idUsuario, Long idJuego, double horas) throws ValidationException {

        List<ErrorModel> errores = new ArrayList<>();

        comprobarIdUsuario(idUsuario, errores);
        comprobarIdJuego(idJuego, errores);

        if (horas < CERO) {
            errores.add(new ErrorModel("horas", TipoErrorEnum.RANGO_INVALIDO));
        }

        comprobarListaErrores(errores);

        BibliotecaEntidad biblioteca = obtenerBiblioteca(idUsuario, idJuego, errores);

        BibliotecaForm actualizarTiempoDeJuego = new BibliotecaForm(
                idUsuario,
                idJuego,
                biblioteca.getFechaAdquisicion(),
                biblioteca.getHorasDeJuego() + horas,
                Instant.now(), // actualizar última sesión
                biblioteca.isEstadoInstalacion()
        );

        bibliotecaRepo.actualizar(biblioteca.getIdBiblioteca(), actualizarTiempoDeJuego);
    }

    // Consultar última sesión

    @Override
    public String consultarUltimaSesion(Long idUsuario, Long idJuego) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        comprobarIdUsuario(idUsuario, errores);
        comprobarIdJuego(idJuego, errores);
        BibliotecaEntidad bibliotecaEntidad = obtenerBiblioteca(idUsuario, idJuego, errores);

        if (bibliotecaEntidad.getUltimaFechaDeJuego() == null) {
            return "Nunca Jugado"; // en realidad enviaría mensaje la vista.
        }
        Instant ultimaFechaHoraDeJuego = bibliotecaEntidad.getUltimaFechaDeJuego();
        Instant horaActual = Instant.now();

        // Diferencia real
        Duration duracion = Duration.between(ultimaFechaHoraDeJuego, horaActual);

        long horasEnTotal = duracion.toHours();

        // Convertimos para mostrar
        ZonedDateTime fechaLocal = ultimaFechaHoraDeJuego
                .atZone(ZoneId.systemDefault());

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        return "Hace " + horasEnTotal
                + " horas de la última vez que jugó, "
                + "que fue: ( "
                + fechaLocal.format(formato)
                + " ).";

    }
    // Filtrar biblioteca

    @Override
    public List<BibliotecaDto> buscarSegunCriterios(Long idUsuario, String texto, Boolean estadoInstalacion) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        comprobarIdUsuario(idUsuario, errores);
        UsuarioEntidad usuarioEntidad = obtenerUsuario(idUsuario, errores);
        List<BibliotecaEntidad> bibliotecaEntidad = bibliotecaRepo.buscarPorUsuario(idUsuario);

        return bibliotecaEntidad.stream()
                .filter(b ->
                        estadoInstalacion == null ||
                                b.isEstadoInstalacion() == estadoInstalacion
                )
                .map(b -> {
                    JuegoEntidad juego = juegoRepo.buscarPorId(b.getIdJuego()).orElseThrow();
                    return new BibliotecaDto(b.getIdBiblioteca(),
                            b.getIdUsuario(),
                            UsuarioEntidadADtoMapper.usuarioEntidadADto(usuarioEntidad),
                            b.getIdJuego(),
                            JuegoEntidadADtoMapper.juegoEntidadADto(juego),
                            b.getFechaAdquisicion(),
                            b.getHorasDeJuego(),
                            b.getUltimaFechaDeJuego(),
                            b.isEstadoInstalacion()
                    );

                })

                .filter(dto -> texto == null || dto.juegoDto().titulo().toLowerCase().contains(texto.toLowerCase()))
                .toList();
    }

    // Ver estadísticas de biblioteca

    @Override
    public EstadisticasBibliotecaDto estadisticasBiblioteca(Long idUsuario) throws ValidationException {

        List<ErrorModel> errores = new ArrayList<>();
        comprobarIdUsuario(idUsuario, errores);

        List<BibliotecaEntidad> biblioteca = bibliotecaRepo.buscarPorUsuario(idUsuario);

        int totalJuegos = biblioteca.size();
        double horasTotales = INICIO_VARIABLE_DOUBLE;
        double valorTotal = INICIO_VARIABLE_DOUBLE;

        String juegoMasJugado = null;
        double maxHoras = INICIO_VARIABLE_NEG;

        List<String> juegosInstalados = new ArrayList<>();
        List<String> juegosNuncaJugados = new ArrayList<>();

        for (BibliotecaEntidad b : biblioteca) {

            JuegoEntidad juego = juegoRepo.buscarPorId(b.getIdJuego()).orElse(null);

            if (juego == null) {
                continue;
            }

            horasTotales += b.getHorasDeJuego();
            valorTotal += juego.getPrecioBase();

            if (b.isEstadoInstalacion()) {
                juegosInstalados.add(juego.getTitulo());
            }

            if (b.getHorasDeJuego() == HORAS_DE_JUEGO_POR_DEFECTO) {
                juegosNuncaJugados.add(juego.getTitulo());
            }

            if (b.getHorasDeJuego() > maxHoras) {
                maxHoras = b.getHorasDeJuego();
                juegoMasJugado = juego.getTitulo();
            }
        }

        return new EstadisticasBibliotecaDto(
                totalJuegos,
                horasTotales,
                juegosInstalados,
                juegoMasJugado,
                valorTotal,
                juegosNuncaJugados
        );
    }

    // Comprobaciones

    private void comprobarIdUsuario(Long idUsuario, List<ErrorModel> errores) throws ValidationException {
        // Compruebo que idUsuario no sea nulo
        if (idUsuario == null) {
            errores.add(new ErrorModel("usuario", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
    }


    private void comprobarIdJuego(Long idJuego, List<ErrorModel> errores) throws ValidationException {
        // Compruebo que idJuego no sea nulo
        if (idJuego == null) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
    }
}
