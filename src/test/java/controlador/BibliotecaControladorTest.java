package controlador;

import org.davidparada.controlador.BibliotecaControlador;
import org.davidparada.controlador.util.ObtenerEntidadesOptional;
import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.BibliotecaDto;
import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.enums.*;
import org.davidparada.modelo.formulario.BibliotecaForm;
import org.davidparada.modelo.formulario.JuegoForm;
import org.davidparada.modelo.formulario.UsuarioForm;
import org.davidparada.repositorio.implementacionMemoria.BibliotecaRepoMemoria;
import org.davidparada.repositorio.implementacionMemoria.JuegoRepoMemoria;
import org.davidparada.repositorio.implementacionMemoria.UsuarioRepoMemoria;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BibliotecaControladorTest {

    private BibliotecaControlador controlador;
    private BibliotecaRepoMemoria bibliotecaRepoMemoria;
    private UsuarioRepoMemoria usuarioRepoMemoria;
    private JuegoRepoMemoria juegoRepoMemoria;

    private Long idUsuario;
    private Long idJuego;

    @BeforeEach
    void setUp() {

        bibliotecaRepoMemoria = new BibliotecaRepoMemoria();
        usuarioRepoMemoria = new UsuarioRepoMemoria();
        juegoRepoMemoria = new JuegoRepoMemoria();

        new ObtenerEntidadesOptional(null, usuarioRepoMemoria, juegoRepoMemoria, bibliotecaRepoMemoria, null);


        UsuarioForm usuarioForm = new UsuarioForm(
                "David",
                "david@test.com",
                "1234",
                "David Parada",
                PaisEnum.ESPANA,
                LocalDate.of(1995, 1, 1),
                Instant.now(),
                "avatar.png",
                100.0,
                EstadoCuentaEnum.ACTIVA
        );

        UsuarioEntidad usuario = usuarioRepoMemoria.crear(usuarioForm);
        idUsuario = usuario.getIdUsuario();

        JuegoForm juegoForm = new JuegoForm(
                "Zelda",
                "Juego aventura",
                "Nintendo",
                LocalDate.of(2020, 1, 1),
                59.99,
                0,
                "Aventura",
                ClasificacionJuegoEnum.PEGI_12,
                new String[]{"ES", "EN"},
                EstadoJuegoEnum.DISPONIBLE
        );

        idJuego = juegoRepoMemoria.crear(juegoForm).getIdJuego();

        controlador = new BibliotecaControlador(
                bibliotecaRepoMemoria,
                juegoRepoMemoria
        );
    }

    // ======================================================
    // VER BIBLIOTECA
    // ======================================================

    @Test
    void verBiblioteca_vacia() throws Exception {
        List<BibliotecaDto> lista =
                controlador.verBiblioteca(idUsuario, null);
        assertTrue(lista.isEmpty());
    }

    @Test
    void verBiblioteca_conJuego() throws Exception {
        controlador.anadirJuego(idUsuario, idJuego);
        List<BibliotecaDto> lista =
                controlador.verBiblioteca(idUsuario, null);
        assertEquals(1, lista.size());
    }

    // ======================================================
    // AÑADIR JUEGO
    // ======================================================

    @Test
    void anadirJuego_usuarioNoExiste() {

        assertThrows(
                ValidationException.class,
                () -> controlador.anadirJuego(999L, idJuego)
        );
    }

    @Test
    void anadirJuego_correcto() throws Exception {
        var dto = controlador.anadirJuego(idUsuario, idJuego);
        assertEquals(idUsuario, dto.idUsuario());
        assertEquals(idJuego, dto.idJuego());
    }

    @Test
    void anadirJuego_duplicado() throws Exception {
        controlador.anadirJuego(idUsuario, idJuego);
        assertThrows(ValidationException.class,
                () -> controlador.anadirJuego(idUsuario, idJuego));
    }

    // ======================================================
    // ELIMINAR JUEGO
    // ======================================================

    @Test
    void eliminarJuego_correcto() throws Exception {
        controlador.anadirJuego(idUsuario, idJuego);
        controlador.eliminarJuego(idUsuario, idJuego);
        assertTrue(controlador.verBiblioteca(idUsuario, null).isEmpty());
    }

    @Test
    void eliminarJuego_noExiste() {
        assertThrows(ValidationException.class,
                () -> controlador.eliminarJuego(idUsuario, idJuego));
    }

    // ======================================================
    // AÑADIR TIEMPO DE JUEGO
    // ======================================================

    @Test
    void actualizarTiempoJuego_entradaValida_tiempoActualizado() throws Exception {

        controlador.anadirJuego(idUsuario, idJuego);

        // añadir tiempo primera vez
        controlador.anadirTiempoDeJuego(idUsuario, idJuego, 5.0);

        // añadir tiempo segunda vez
        controlador.anadirTiempoDeJuego(idUsuario, idJuego, 3.0);

        var entidad = bibliotecaRepoMemoria.buscarPorUsuarioYJuego(idUsuario, idJuego);

        assertTrue(entidad.isPresent());
        assertEquals(8.0, entidad.get().getHorasDeJuego());
    }

    @Test
    void anadirTiempoNegativo() throws Exception {

        controlador.anadirJuego(idUsuario, idJuego);

        assertThrows(
                ValidationException.class,
                () -> controlador.anadirTiempoDeJuego(idUsuario, idJuego, -5.0)
        );
    }

    @Test
    void anadirTiempo_correcto() throws Exception {

        controlador.anadirJuego(idUsuario, idJuego);

        var entidad =
                bibliotecaRepoMemoria.buscarPorUsuarioYJuego(idUsuario, idJuego);

        BibliotecaForm form = new BibliotecaForm(
                idUsuario,
                idJuego,
                entidad.get().getFechaAdquisicion(),
                entidad.get().getHorasDeJuego(),
                entidad.get().getUltimaFechaDeJuego(),
                entidad.get().isEstadoInstalacion()
        );

        controlador.anadirTiempoDeJuego(idUsuario, idJuego, 5.0);

        var actualizada =
                bibliotecaRepoMemoria.buscarPorUsuarioYJuego(idUsuario, idJuego);

        assertEquals(5.0, actualizada.get().getHorasDeJuego());
    }

    @Test
    void anadirTiempo_horasNull() {
        assertThrows(ValidationException.class,
                () -> controlador.anadirTiempoDeJuego(idUsuario, idJuego, 0));
    }

    // ======================================================
    // BUSCAR SEGÚN CRITERIOS
    // ======================================================

    @Test
    void buscarSegunCriterios_filtraPorEstadoInstalacion() throws Exception {

        controlador.anadirJuego(idUsuario, idJuego);

        var entidad = bibliotecaRepoMemoria.buscarPorUsuarioYJuego(idUsuario, idJuego);

        BibliotecaForm form = new BibliotecaForm(
                idUsuario,
                idJuego,
                entidad.get().getFechaAdquisicion(),
                entidad.get().getHorasDeJuego(),
                entidad.get().getUltimaFechaDeJuego(),
                true
        );

        bibliotecaRepoMemoria.actualizar(entidad.get().getIdBiblioteca(), form);

        List<BibliotecaDto> resultado =
                controlador.buscarSegunCriterios(idUsuario, null, true);

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).estadoInstalacion());
    }

    @Test
    void buscarSegunCriterios_filtraPorTexto() throws Exception {

        controlador.anadirJuego(idUsuario, idJuego);

        List<BibliotecaDto> resultado =
                controlador.buscarSegunCriterios(idUsuario, "zel", null);

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).juegoDto().titulo().toLowerCase().contains("zel"));
    }

    @Test
    void buscarSegunCriterios_sinResultados() throws Exception {

        controlador.anadirJuego(idUsuario, idJuego);

        List<BibliotecaDto> resultado =
                controlador.buscarSegunCriterios(idUsuario, "otro", null);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarSegunCriterios_usuarioInvalido() {

        assertThrows(ValidationException.class,
                () -> controlador.buscarSegunCriterios(999L, null, null));
    }


    // ======================================================
    // ESTADÍSTICAS BIBLIOTECA
    // ======================================================

    @Test
    void estadisticasBiblioteca_calculoCompleto() throws Exception {

        controlador.anadirJuego(idUsuario, idJuego);

        var entidad = bibliotecaRepoMemoria.buscarPorUsuarioYJuego(idUsuario, idJuego);

        BibliotecaForm form = new BibliotecaForm(
                idUsuario,
                idJuego,
                entidad.get().getFechaAdquisicion(),
                5.0,
                entidad.get().getUltimaFechaDeJuego(),
                true
        );

        bibliotecaRepoMemoria.actualizar(entidad.get().getIdBiblioteca(), form);

        var stats = controlador.estadisticasBiblioteca(idUsuario);

        assertEquals(1, stats.totalDeJuegos());
        assertEquals(5.0, stats.horasTotales());
        assertEquals(1, stats.juegosInstalados().size());
        assertEquals("Zelda", stats.juegoMasJugado());
        assertEquals(59.99, stats.valorTotal());
        assertTrue(stats.juegosNuncaJugados().isEmpty());
    }

    @Test
    void estadisticasBiblioteca_nuncaJugado() throws Exception {

        controlador.anadirJuego(idUsuario, idJuego);

        var stats = controlador.estadisticasBiblioteca(idUsuario);

        assertEquals(1, stats.totalDeJuegos());
        assertEquals(0.0, stats.horasTotales());
        assertEquals(1, stats.juegosNuncaJugados().size());
        assertEquals("Zelda", stats.juegosNuncaJugados().get(0));
    }

    @Test
    void estadisticasBiblioteca_bibliotecaVacia() throws Exception {

        var stats = controlador.estadisticasBiblioteca(idUsuario);

        assertEquals(0, stats.totalDeJuegos());
        assertEquals(0.0, stats.horasTotales());
        assertTrue(stats.juegosInstalados().isEmpty());
        assertNull(stats.juegoMasJugado());
        assertEquals(0.0, stats.valorTotal());
        assertTrue(stats.juegosNuncaJugados().isEmpty());
    }

}
