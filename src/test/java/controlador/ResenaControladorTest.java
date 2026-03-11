package controlador;

import org.davidparada.controlador.ResenaControlador;
import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.ResenaDto;
import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.entidad.ResenaEntidad;
import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.enums.*;
import org.davidparada.modelo.formulario.JuegoForm;
import org.davidparada.modelo.formulario.ResenaForm;
import org.davidparada.modelo.formulario.UsuarioForm;
import org.davidparada.repositorio.implementacionMemoria.JuegoRepo;
import org.davidparada.repositorio.implementacionMemoria.ResenaRepo;
import org.davidparada.repositorio.implementacionMemoria.UsuarioRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResenaControladorTest {

    private ResenaControlador controlador;
    private UsuarioRepo usuarioRepo;
    private JuegoRepo juegoRepo;
    private ResenaRepo resenaRepo;

    private UsuarioEntidad usuario;
    private JuegoEntidad juego;

    @BeforeEach
    void setUp() throws ValidationException {

        usuarioRepo = new UsuarioRepo();
        juegoRepo = new JuegoRepo();
        resenaRepo = new ResenaRepo();

        controlador = new ResenaControlador(resenaRepo, usuarioRepo, juegoRepo);

        // ===== Crear Usuario =====
        usuario = usuarioRepo.crear(
                new UsuarioForm(
                        "david",
                        "david@email.com",
                        "1234",
                        "David Parada",
                        PaisEnum.ESPANA,
                        LocalDate.of(2000, 1, 1),
                        Instant.now(),
                        "avatar.png",
                        100.0,
                        EstadoCuentaEnum.ACTIVA
                )
        );

        // ===== Crear Juego =====
        juego = juegoRepo.crear(
                new JuegoForm(
                        "Elden Ring",
                        "Juego RPG",
                        "FromSoftware",
                        LocalDate.of(2022, 2, 25),
                        60.0,
                        0,
                        "RPG",
                        ClasificacionJuegoEnum.PEGI_18,
                        new String[]{"Español", "Inglés"},
                        EstadoJuegoEnum.DISPONIBLE
                )
        );
    }

    // ==============================
    // ESCRIBIR RESEÑA
    // ==============================

    @Test
    void escribirResena_ok() throws Exception {

        ResenaForm form = new ResenaForm(
                usuario.getIdUsuario(),
                juego.getIdJuego(),
                true,
                "Gran juego",
                40.0,
                Instant.now(),
                null,
                EstadoPublicacionEnum.PUBLICADA
        );

        ResenaDto dto = controlador.escribirResena(form);

        assertNotNull(dto);
        assertEquals("Gran juego", dto.textoResena());
        assertEquals(usuario.getIdUsuario(), dto.idUsuario());
        assertEquals(EstadoPublicacionEnum.PUBLICADA, dto.estadoPublicacion());
    }

    @Test
    void escribirResena_usuarioNoExiste() {

        ResenaForm form = new ResenaForm(
                999L,
                juego.getIdJuego(),
                true,
                "Texto",
                10.0,
                Instant.now(),
                null,
                EstadoPublicacionEnum.PUBLICADA
        );

        assertThrows(ValidationException.class,
                () -> controlador.escribirResena(form));
    }

    @Test
    void escribirResena_juegoNoExiste() {

        ResenaForm form = new ResenaForm(
                usuario.getIdUsuario(),
                999L,
                true,
                "Texto",
                10.0,
                Instant.now(),
                null,
                EstadoPublicacionEnum.PUBLICADA
        );

        assertThrows(ValidationException.class,
                () -> controlador.escribirResena(form));
    }

    // ==============================
    // ELIMINAR RESEÑA
    // ==============================

    @Test
    void eliminarResena_ok() throws Exception {

        ResenaEntidad resena = resenaRepo.crear(
                new ResenaForm(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        true,
                        "Eliminar",
                        5.0,
                        Instant.now(),
                        null,
                        EstadoPublicacionEnum.PUBLICADA
                )
        );

        boolean eliminado = controlador.eliminarResena(resena.getIdResena());

        assertTrue(eliminado);
    }

    @Test
    void eliminarResena_idNull() {
        assertThrows(ValidationException.class,
                () -> controlador.eliminarResena(null));
    }

    @Test
    void eliminarResena_noExiste() {
        assertThrows(ValidationException.class,
                () -> controlador.eliminarResena(999L));
    }

    // ==============================
    // OCULTAR RESEÑA
    // ==============================

    @Test
    void ocultarResena_ok() throws Exception {

        ResenaEntidad resena = resenaRepo.crear(
                new ResenaForm(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        true,
                        "Ocultar",
                        20.0,
                        Instant.now(),
                        null,
                        EstadoPublicacionEnum.PUBLICADA
                )
        );

        ResenaDto dto = controlador.ocultarResena(resena.getIdResena());

        assertEquals(EstadoPublicacionEnum.OCULTA, dto.estadoPublicacion());
        assertNotNull(dto.fechaUltimaEdicion());
    }

    @Test
    void ocultarResena_noExiste() {
        assertThrows(ValidationException.class,
                () -> controlador.ocultarResena(999L));
    }

    // ==============================
    // OBTENER RESEÑAS POR JUEGO
    // ==============================

    @Test
    void obtenerResenas_ok() throws Exception {

        resenaRepo.crear(
                new ResenaForm(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        true,
                        "Reseña 1",
                        10.0,
                        Instant.now(),
                        null,
                        EstadoPublicacionEnum.PUBLICADA
                )
        );

        List<ResenaDto> lista = controlador.obtenerResenas(juego.getIdJuego());

        assertEquals(1, lista.size());
        assertEquals("Reseña 1", lista.get(0).textoResena());
    }

    @Test
    void obtenerResenas_listaVacia() throws Exception {

        List<ResenaDto> lista = controlador.obtenerResenas(juego.getIdJuego());

        assertTrue(lista.isEmpty());
    }

    // ==============================
    // RESEÑAS POR USUARIO
    // ==============================

    @Test
    void obtenerResenasUsuario_ok() throws Exception {

        resenaRepo.crear(
                new ResenaForm(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        true,
                        "Usuario reseña",
                        15.0,
                        Instant.now(),
                        null,
                        EstadoPublicacionEnum.PUBLICADA
                )
        );

        List<ResenaDto> lista =
                controlador.obtenerResenasUsuario(usuario.getIdUsuario());

        assertEquals(1, lista.size());
        assertEquals("Usuario reseña", lista.get(0).textoResena());
    }

    @Test
    void obtenerResenasUsuario_noExiste() {
        assertThrows(ValidationException.class,
                () -> controlador.obtenerResenasUsuario(999L));
    }
}