package controlador;

import org.davidparada.controlador.ResenaControlador;
import org.davidparada.controlador.util.ObtenerEntidadesOptional;
import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.ResenaDto;
import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.entidad.ResenaEntidad;
import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.enums.*;
import org.davidparada.modelo.formulario.BibliotecaForm;
import org.davidparada.modelo.formulario.JuegoForm;
import org.davidparada.modelo.formulario.ResenaForm;
import org.davidparada.modelo.formulario.UsuarioForm;
import org.davidparada.repositorio.implementacionMemoria.BibliotecaRepoMemoria;
import org.davidparada.repositorio.implementacionMemoria.JuegoRepoMemoria;
import org.davidparada.repositorio.implementacionMemoria.ResenaRepoMemoria;
import org.davidparada.repositorio.implementacionMemoria.UsuarioRepoMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResenaControladorTest {

    private ResenaControlador controlador;
    private UsuarioRepoMemoria usuarioRepoMemoria;
    private JuegoRepoMemoria juegoRepoMemoria;
    private ResenaRepoMemoria resenaRepoMemoria;
    private BibliotecaRepoMemoria bibliotecaRepoMemoria;
    private UsuarioEntidad usuario;
    private JuegoEntidad juego;

    @BeforeEach
    void setUp() throws ValidationException {

        usuarioRepoMemoria = new UsuarioRepoMemoria();
        juegoRepoMemoria = new JuegoRepoMemoria();
        resenaRepoMemoria = new ResenaRepoMemoria();
        bibliotecaRepoMemoria = new BibliotecaRepoMemoria();

        controlador = new ResenaControlador(
                resenaRepoMemoria
        );
        new ObtenerEntidadesOptional(null, usuarioRepoMemoria, juegoRepoMemoria, bibliotecaRepoMemoria, resenaRepoMemoria);

        // ===== Crear Usuario =====
        usuario = usuarioRepoMemoria.crear(
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
        juego = juegoRepoMemoria.crear(
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

        bibliotecaRepoMemoria.crear(
                new BibliotecaForm(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        Instant.now(),
                        0.0,
                        null,
                        false
                )
        );

        ResenaDto dto = controlador.escribirResena(
                usuario.getIdUsuario(),
                juego.getIdJuego(),
                true,
                "Este es un gran juego con muchas horas de diversión y contenido"
        );

        assertNotNull(dto);
        assertEquals(usuario.getIdUsuario(), dto.idUsuario());
    }

    @Test
    void noPermiteDosResenasMismoJuego() throws Exception {
        bibliotecaRepoMemoria.crear(
                new BibliotecaForm(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        Instant.now(),
                        0.0,
                        null,
                        false
                )
        );

        controlador.escribirResena(
                usuario.getIdUsuario(),
                juego.getIdJuego(),
                true,
                "Texto suficientemente largo para cumplir validacion de longitud"
        );

        assertThrows(
                ValidationException.class,
                () -> controlador.escribirResena(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        true,
                        "Texto suficientemente largo para cumplir validacion de longitud"
                )
        );
    }

    @Test
    void textoResenaDemasiadoCorto() {

        assertThrows(
                ValidationException.class,
                () -> controlador.escribirResena(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        true,
                        "corto"
                )
        );
    }

    @Test
    void escribirResena_usuarioNoExiste() {

        assertThrows(
                ValidationException.class,
                () -> controlador.escribirResena(
                        999L,
                        juego.getIdJuego(),
                        true,
                        "Texto suficientemente largo para cumplir validacion"
                )
        );
    }

    @Test
    void escribirResena_juegoNoExiste() {

        assertThrows(
                ValidationException.class,
                () -> controlador.escribirResena(
                        usuario.getIdUsuario(),
                        999L,
                        true,
                        "Texto suficientemente largo para cumplir validacion"
                )
        );
    }

    // ==============================
    // ELIMINAR RESEÑA
    // ==============================

    @Test
    void eliminarResena_ok() throws Exception {

        ResenaEntidad resena = crearResenaBase();

        boolean eliminado =
                controlador.eliminarResena(
                        resena.getIdResena(),
                        resena.getIdUsuario()
                );

        assertTrue(eliminado);
    }

    @Test
    void eliminarResena_idResenaNull() {
        assertThrows(ValidationException.class,
                () -> controlador.eliminarResena(null, usuario.getIdUsuario()));
    }

    @Test
    void eliminarResena_idUsuarioNull() {
        assertThrows(ValidationException.class,
                () -> controlador.eliminarResena(1L, null));
    }

    @Test
    void eliminarResena_noExiste() {
        assertThrows(ValidationException.class,
                () -> controlador.eliminarResena(999L, usuario.getIdUsuario()));
    }

    // ==============================
    // OCULTAR RESEÑA
    // ==============================

    @Test
    void ocultarResena_ok() throws Exception {

        ResenaEntidad resena = crearResenaBase();

        ResenaDto dto =
                controlador.ocultarResena(
                        resena.getIdResena(),
                        usuario.getIdUsuario()
                );

        assertEquals(EstadoPublicacionEnum.OCULTA, dto.estadoPublicacion());
        assertNotNull(dto.fechaUltimaEdicion());
    }

    @Test
    void ocultarResena_noExiste() {
        assertThrows(ValidationException.class,
                () -> controlador.ocultarResena(999L, usuario.getIdUsuario()));
    }

    // ==============================
    // OBTENER RESEÑAS POR JUEGO
    // ==============================

    @Test
    void obtenerResenas_ok() throws Exception {

        crearResenaBase();

        List<ResenaDto> lista = controlador.obtenerResenas(
                juego.getIdJuego(),
                true,
                OrdenarResenaEnum.RECIENTES
        );

        assertEquals(1, lista.size());
    }

    @Test
    void obtenerResenas_listaVacia() throws Exception {

        List<ResenaDto> lista = controlador.obtenerResenas(
                juego.getIdJuego(),
                true,
                OrdenarResenaEnum.RECIENTES
        );

        assertTrue(lista.isEmpty());
    }

    @Test
    void obtenerResenas_filtraNoRecomendadas() throws Exception {

        resenaRepoMemoria.crear(
                new ResenaForm(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        false,
                        "Este juego no me ha gustado porque tiene muchos errores",
                        10.0,
                        Instant.now(),
                        null,
                        EstadoPublicacionEnum.PUBLICADA
                )
        );

        List<ResenaDto> lista = controlador.obtenerResenas(
                juego.getIdJuego(),
                true,
                OrdenarResenaEnum.RECIENTES
        );

        assertTrue(lista.isEmpty());
    }

    // ==============================
    // RESEÑAS POR USUARIO
    // ==============================

    @Test
    void obtenerResenasUsuario_ok() throws Exception {

        crearResenaBase();

        List<ResenaDto> lista =
                controlador.obtenerResenasUsuario(usuario.getIdUsuario());

        assertEquals(1, lista.size());
    }

    @Test
    void obtenerResenasUsuario_noExiste() {
        assertThrows(ValidationException.class,
                () -> controlador.obtenerResenasUsuario(999L));
    }

    // ==============================
    // TEST DEL PROFESOR
    // ==============================

    @Test
    void crearResena_TextoVacio_LanzaValidationException() {

        assertThrows(
                ValidationException.class,
                () -> controlador.escribirResena(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        true,
                        ""
                )
        );
    }

    @Test
    void crearResena_TextoMenor50Caracteres_LanzaValidationException() {

        assertThrows(
                ValidationException.class,
                () -> controlador.escribirResena(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        true,
                        "Texto corto de prueba"
                )
        );
    }

    @Test
    void crearResena_TextoMayor8000Caracteres_LanzaValidationException() {

        String texto = "a".repeat(8001);

        assertThrows(
                ValidationException.class,
                () -> controlador.escribirResena(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        true,
                        texto
                )
        );
    }

    @Test
    void crearResena_UsuarioSinJuegoEnBiblioteca_LanzaValidationException() {

        UsuarioEntidad otroUsuario = usuarioRepoMemoria.crear(
                new UsuarioForm(
                        "otro",
                        "otro@email.com",
                        "1234",
                        "Otro Usuario",
                        PaisEnum.ESPANA,
                        LocalDate.of(2000, 1, 1),
                        Instant.now(),
                        null,
                        0.0,
                        EstadoCuentaEnum.ACTIVA
                )
        );

        assertThrows(
                ValidationException.class,
                () -> controlador.escribirResena(
                        otroUsuario.getIdUsuario(),
                        juego.getIdJuego(),
                        true,
                        "Texto suficientemente largo para que pase validacion minima de caracteres en reseña"
                )
        );
    }


// ======================================================
// ELIMINAR RESEÑA - VALIDACIONES EXTRA
// ======================================================

    @Test
    void eliminarResena_UsuarioNoEsDuenio_LanzaValidationException() throws Exception {

        ResenaEntidad resena = crearResenaBase();

        UsuarioEntidad otroUsuario = usuarioRepoMemoria.crear(
                new UsuarioForm(
                        "otro",
                        "otro@test.com",
                        "1234",
                        "Otro",
                        PaisEnum.ESPANA,
                        LocalDate.of(2000, 1, 1),
                        Instant.now(),
                        null,
                        0.0,
                        EstadoCuentaEnum.ACTIVA
                )
        );

        assertThrows(
                ValidationException.class,
                () -> controlador.eliminarResena(
                        resena.getIdResena(),
                        otroUsuario.getIdUsuario()
                )
        );
    }


// ======================================================
// OCULTAR RESEÑA - VALIDACIONES EXTRA
// ======================================================

    @Test
    void ocultarResena_IdInvalido_LanzaValidationException() {

        assertThrows(
                ValidationException.class,
                () -> controlador.ocultarResena(null, usuario.getIdUsuario())
        );
    }

    @Test
    void ocultarResena_UsuarioNoEsDuenio_LanzaValidationException() throws Exception {

        ResenaEntidad resena = crearResenaBase();

        UsuarioEntidad otroUsuario = usuarioRepoMemoria.crear(
                new UsuarioForm(
                        "otro2",
                        "otro2@test.com",
                        "1234",
                        "Otro",
                        PaisEnum.ESPANA,
                        LocalDate.of(2000, 1, 1),
                        Instant.now(),
                        null,
                        0.0,
                        EstadoCuentaEnum.ACTIVA
                )
        );

        assertThrows(
                ValidationException.class,
                () -> controlador.ocultarResena(
                        resena.getIdResena(),
                        otroUsuario.getIdUsuario()
                )
        );
    }


// ======================================================
// LISTAR RESEÑAS USUARIO - CASOS FALTANTES
// ======================================================

    @Test
    void listarResenasPorUsuario_UsuarioSinResenas_RetornaListaVacia() throws Exception {

        UsuarioEntidad nuevoUsuario = usuarioRepoMemoria.crear(
                new UsuarioForm(
                        "nuevo",
                        "nuevo@test.com",
                        "1234",
                        "Nuevo Usuario",
                        PaisEnum.ESPANA,
                        LocalDate.of(2000, 1, 1),
                        Instant.now(),
                        null,
                        0.0,
                        EstadoCuentaEnum.ACTIVA
                )
        );

        List<ResenaDto> lista =
                controlador.obtenerResenasUsuario(nuevoUsuario.getIdUsuario());

        assertTrue(lista.isEmpty());
    }

// ==========
// HELPER
// ==========

    private ResenaEntidad crearResenaBase() {

        return resenaRepoMemoria.crear(
                new ResenaForm(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        true,
                        "a".repeat(60), // texto válido >=50
                        10.0,
                        Instant.now(),
                        null,
                        EstadoPublicacionEnum.PUBLICADA
                )
        );
    }
}