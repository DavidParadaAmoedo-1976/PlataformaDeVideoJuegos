package org.DavidParada.controlador;

import org.DavidParada.excepcion.ValidationException;
import org.DavidParada.modelo.dto.BibliotecaDto;
import org.DavidParada.modelo.dto.EstadisticasBibliotecaDto;
import org.DavidParada.modelo.entidad.BibliotecaEntidad;
import org.DavidParada.modelo.entidad.JuegoEntidad;
import org.DavidParada.modelo.entidad.UsuarioEntidad;
import org.DavidParada.modelo.enums.OrdenarJuegosBibliotecaEnum;
import org.DavidParada.modelo.enums.TipoErrorEnum;
import org.DavidParada.modelo.formulario.BibliotecaForm;
import org.DavidParada.modelo.formulario.validacion.ErrorModel;
import org.DavidParada.modelo.mapper.BibliotecaEntidadADtoMapper;
import org.DavidParada.modelo.mapper.JuegoEntidadADtoMapper;
import org.DavidParada.modelo.mapper.UsuarioEntidadADtoMapper;
import org.DavidParada.repositorio.interfaces.IBibliotecaRepo;
import org.DavidParada.repositorio.interfaces.IJuegoRepo;
import org.DavidParada.repositorio.interfaces.IUsuarioRepo;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BibliotecaControlador {

    private final IBibliotecaRepo bibliotecaRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IJuegoRepo juegoRepo;

    public BibliotecaControlador(IBibliotecaRepo bibliotecaRepo,
                                 IUsuarioRepo usuarioRepo,
                                 IJuegoRepo juegoRepo) {

        this.bibliotecaRepo = bibliotecaRepo;
        this.usuarioRepo = usuarioRepo;
        this.juegoRepo = juegoRepo;
    }

    // Ver Biblioteca personal

    public List<BibliotecaDto> verBiblioteca(Long idUsuario, OrdenarJuegosBibliotecaEnum orden) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        comprobarIdUsuario(idUsuario, errores);
        comprobarListaErrores(errores);

        List<BibliotecaEntidad> juegosEntidad = bibliotecaRepo.buscarPorUsuario(idUsuario); // Guarda todos los juegos de la biblioteca del usuario en una lista.

        // Mapea la lista de Entidad a un DTO para poder mostrar los datos del juego
        List<BibliotecaDto> juegos = juegosEntidad.stream()
                .map(b -> {
                    JuegoEntidad juegoEntidad = juegoRepo.buscarPorId(b.getIdJuego());
                    return new BibliotecaDto(b.getIdBiblioteca(),
                            b.getIdUsuario(),
                            UsuarioEntidadADtoMapper.usuarioEntidadADto(usuarioRepo.buscarPorId(b.getIdUsuario())),
                            b.getIdJuego(),
                            JuegoEntidadADtoMapper.juegoEntidadADto(juegoEntidad),
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
            }
        }
        return juegos;
    }

    // Añadir juego a biblioteca

    public BibliotecaDto anadirJuego(Long idUsuario, Long idJuego) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        comprobarIdUsuario(idUsuario, errores);
        comprobarIdJuego(idJuego, errores);
        comprobarListaErrores(errores);

        List<BibliotecaEntidad> bibliotecasEntidad = bibliotecaRepo.buscarPorUsuario(idUsuario);

        Boolean yaTieneJuego = bibliotecasEntidad.stream().anyMatch(b -> b.getIdJuego().equals(idJuego));

        if (yaTieneJuego) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.DUPLICADO));
            throw new ValidationException(errores);
        }
        BibliotecaForm nuevoJuego = new BibliotecaForm(
                idUsuario,
                idJuego,
                Instant.now(),
                0.0,
                null,
                false
        );

        BibliotecaEntidad nuevoJuegoEntidad = bibliotecaRepo.crear(nuevoJuego);
        UsuarioEntidad usuarioEntidad = usuarioRepo.buscarPorId(idUsuario);
        JuegoEntidad juegoEntidad = juegoRepo.buscarPorId(idJuego);

        return BibliotecaEntidadADtoMapper.bibliotecaEntidadADto(nuevoJuegoEntidad, usuarioEntidad, juegoEntidad);
    }

    // Eliminar juego de biblioteca

    public void eliminarJuego(Long idUsuario, Long idJuego) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        comprobarIdUsuario(idUsuario, errores);
        comprobarIdJuego(idJuego, errores);
        comprobarListaErrores(errores);

        BibliotecaEntidad bibliotecaEntidad = bibliotecaRepo.buscarPorUsuarioYJuego(idUsuario, idJuego);

        if (bibliotecaEntidad == null) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.NO_ENCONTRADO));
            throw new ValidationException(errores);
        }
        bibliotecaRepo.eliminar(bibliotecaEntidad.getIdBiblioteca());
    }

    // Actualizar tiempo de juego

    public void anadirTiempoDeJuego(Long idUsuario, Long idJuego, Double horas, BibliotecaForm form) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();


        comprobarIdUsuario(idUsuario, errores);
        comprobarIdJuego(idJuego, errores);

        if (horas == null) {
            errores.add(new ErrorModel("horasDeJuego", TipoErrorEnum.OBLIGATORIO));
        }

        if (form == null) {
            errores.add(new ErrorModel("formulario", TipoErrorEnum.NO_ENCONTRADO));
        }

        BibliotecaEntidad biblioteca = bibliotecaRepo.buscarPorUsuarioYJuego(idUsuario, idJuego);
        if (biblioteca == null) {
            errores.add(new ErrorModel("biblioteca", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);

        BibliotecaForm actualizarTiempoDeJuego = new BibliotecaForm(
                idUsuario,
                idJuego,
                form.getFechaAdquisicion(),
                biblioteca.getHorasDeJuego() + horas,
                form.getUltimaFechaDeJuego(),
                form.isEstadoInstalacion()
        );

        bibliotecaRepo.actualizar(biblioteca.getIdBiblioteca(), actualizarTiempoDeJuego);
    }

    // Consultar última sesión

    public String consultarUltimaSesion(Long idUsuario, Long idJuego) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        comprobarIdUsuario(idUsuario, errores);
        comprobarIdJuego(idJuego, errores);
        BibliotecaEntidad bibliotecaEntidad = bibliotecaRepo.buscarPorUsuarioYJuego(idUsuario, idJuego);

        if (bibliotecaEntidad == null) {
            errores.add(new ErrorModel("biblioteca", TipoErrorEnum.NO_ENCONTRADO));
        }

        comprobarListaErrores(errores);

        if (bibliotecaEntidad.getUltimaFechaDeJuego() == null) {
            return "Nunca Jugado"; // en realidad enviaría mensaje la vista.
        }
        Instant ultimaFechaHoraDeJuego = bibliotecaEntidad.getUltimaFechaDeJuego();
        Instant horaActual = Instant.now();

        // Diferencia real
        Duration duracion = Duration.between(ultimaFechaHoraDeJuego, horaActual);

        Long horasEnTotal = duracion.toHours();

        // Convertimos para mostrar
        ZonedDateTime fechaLocal = ultimaFechaHoraDeJuego
                .atZone(ZoneId.systemDefault());

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        String mensaje = "Hace " + horasEnTotal
                + " horas de la última vez que jugó, "
                + "que fue: ( "
                + fechaLocal.format(formatter)
                + " ).";

        return mensaje;

    }
    // Filtrar biblioteca

    public List<BibliotecaDto> buscarSegunCriterios(Long idUsuario, String texto, Boolean estadoInstalacion) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        comprobarIdUsuario(idUsuario, errores);
        comprobarListaErrores(errores);
        List<BibliotecaEntidad> bibliotecaEntidad = bibliotecaRepo.buscarPorUsuario(idUsuario);

        return bibliotecaEntidad.stream()
                .filter(b ->
                        estadoInstalacion == null ||
                                b.isEstadoInstalacion() == estadoInstalacion
                )
                .map(b -> {
                    return new BibliotecaDto(b.getIdBiblioteca(),
                            b.getIdUsuario(),
                            UsuarioEntidadADtoMapper.usuarioEntidadADto(usuarioRepo.buscarPorId(b.getIdUsuario())),
                            b.getIdJuego(),
                            JuegoEntidadADtoMapper.juegoEntidadADto(juegoRepo.buscarPorId(b.getIdJuego())),
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

    public EstadisticasBibliotecaDto estadisticasBiblioteca(Long idUsuario) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        comprobarIdUsuario(idUsuario, errores);
        comprobarListaErrores(errores);

        List<BibliotecaEntidad> bibliotecaEntidad = bibliotecaRepo.buscarPorUsuario(idUsuario);

        Integer totalJuegos = bibliotecaEntidad.size();
        Double horasTotales = bibliotecaEntidad.stream()
                .mapToDouble(j -> j.getHorasDeJuego())
                .sum();
        List<String> juegosInstalados = bibliotecaEntidad.stream()
                .filter(b -> b.isEstadoInstalacion())
                .map(b -> juegoRepo.buscarPorId(b.getIdJuego()).getTitulo())
                .toList();
        String juegoMasJugado = bibliotecaEntidad.stream()
                .max(Comparator.comparing(b -> b.getHorasDeJuego()))
                .map(j -> juegoRepo.buscarPorId(j.getIdJuego()).getTitulo())
                .orElse(null);
        Double valorTotal = bibliotecaEntidad.stream()
                .mapToDouble(b -> juegoRepo.buscarPorId(b.getIdJuego()).getPrecioBase())
                .sum();
        List<String> juegosNuncaJugados = bibliotecaEntidad.stream()
                .filter(b -> b.getHorasDeJuego() == (0.0))
                .map(b -> juegoRepo.buscarPorId(b.getIdJuego()).getTitulo())
                .toList();

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

    private UsuarioEntidad comprobarIdUsuario(Long idUsuario, List<ErrorModel> errores) {

        if (idUsuario == null) {
            errores.add(new ErrorModel("usuario", TipoErrorEnum.OBLIGATORIO));
            return null;
        }

        UsuarioEntidad usuario = usuarioRepo.buscarPorId(idUsuario);

        if (usuario == null) {
            errores.add(new ErrorModel("usuario", TipoErrorEnum.NO_ENCONTRADO));
        }

        return usuario;
    }


    private void comprobarIdJuego(Long idJuego, List<ErrorModel> errores) {
        // Compruebo que idJuego no se nulo
        if (idJuego == null) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.OBLIGATORIO));
        }
        // Compruebo que juego exista
        JuegoEntidad juego = juegoRepo.buscarPorId(idJuego);
        if (juego == null) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.NO_ENCONTRADO));
        }
    }

    private void comprobarListaErrores(List<ErrorModel> errores) throws ValidationException {
        if (!errores.isEmpty()) {
            throw new ValidationException(errores);
        }
    }
}
