package controlador;

import org.DavidParada.controlador.BibliotecaControlador;
import org.DavidParada.excepcion.ValidationException;
import org.DavidParada.modelo.dto.BibliotecaDto;
import org.DavidParada.modelo.entidad.UsuarioEntidad;
import org.DavidParada.modelo.enums.*;
import org.DavidParada.modelo.formulario.BibliotecaForm;
import org.DavidParada.modelo.formulario.JuegoForm;
import org.DavidParada.modelo.formulario.UsuarioForm;
import org.DavidParada.repositorio.implementacionMemoria.BibliotecaRepo;
import org.DavidParada.repositorio.implementacionMemoria.JuegoRepo;
import org.DavidParada.repositorio.implementacionMemoria.UsuarioRepo;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BibliotecaControladorTest {

    private BibliotecaControlador controlador;
    private BibliotecaRepo bibliotecaRepo;
    private UsuarioRepo usuarioRepo;
    private JuegoRepo juegoRepo;

    private Long idUsuario;
    private Long idJuego;

    @BeforeEach
    void setUp() {

        bibliotecaRepo = new BibliotecaRepo();
        usuarioRepo = new UsuarioRepo();
        juegoRepo = new JuegoRepo();

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

        UsuarioEntidad usuario = usuarioRepo.crear(usuarioForm);
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

        idJuego = juegoRepo.crear(juegoForm).getIdJuego();

        controlador = new BibliotecaControlador(
                bibliotecaRepo,
                usuarioRepo,
                juegoRepo
        );
    }

    // ======================================================
    // 1️⃣ VER BIBLIOTECA
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
    // 2️⃣ AÑADIR JUEGO
    // ======================================================

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
    // 3️⃣ ELIMINAR JUEGO
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
    // 4️⃣ AÑADIR TIEMPO DE JUEGO
    // ======================================================

    @Test
    void anadirTiempo_correcto() throws Exception {

        controlador.anadirJuego(idUsuario, idJuego);

        var entidad =
                bibliotecaRepo.buscarPorUsuarioYJuego(idUsuario, idJuego);

        BibliotecaForm form = new BibliotecaForm(
                idUsuario,
                idJuego,
                entidad.getFechaAdquisicion(),
                entidad.getHorasDeJuego(),
                entidad.getUltimaFechaDeJuego(),
                entidad.isEstadoInstalacion()
        );

        controlador.anadirTiempoDeJuego(idUsuario, idJuego, 5.0, form);

        var actualizada =
                bibliotecaRepo.buscarPorUsuarioYJuego(idUsuario, idJuego);

        assertEquals(5.0, actualizada.getHorasDeJuego());
    }

    @Test
    void anadirTiempo_horasNull() {
        assertThrows(ValidationException.class,
                () -> controlador.anadirTiempoDeJuego(idUsuario, idJuego, null, null));
    }

    // ======================================================
    // 5️⃣ CONSULTAR ÚLTIMA SESIÓN
    // ======================================================

    @Test
    void consultarUltimaSesion_nuncaJugado() throws Exception {
        controlador.anadirJuego(idUsuario, idJuego);
        String mensaje =
                controlador.consultarUltimaSesion(idUsuario, idJuego);
        assertEquals("Nunca Jugado", mensaje);
    }

    @Test
    void consultarUltimaSesion_conFecha() throws Exception {

        controlador.anadirJuego(idUsuario, idJuego);

        var entidad =
                bibliotecaRepo.buscarPorUsuarioYJuego(idUsuario, idJuego);

        BibliotecaForm form = new BibliotecaForm(
                idUsuario,
                idJuego,
                entidad.getFechaAdquisicion(),
                entidad.getHorasDeJuego(),
                Instant.now().minusSeconds(3600),
                entidad.isEstadoInstalacion()
        );

        bibliotecaRepo.actualizar(entidad.getIdBiblioteca(), form);

        String mensaje =
                controlador.consultarUltimaSesion(idUsuario, idJuego);

        assertTrue(mensaje.contains("Hace"));
    }

    // ======================================================
    // 6️⃣ BUSCAR SEGÚN CRITERIOS
    // ======================================================

    @Test
    void buscarSegunCriterios_filtraPorEstadoInstalacion() throws Exception {

        controlador.anadirJuego(idUsuario, idJuego);

        var entidad = bibliotecaRepo.buscarPorUsuarioYJuego(idUsuario, idJuego);

        BibliotecaForm form = new BibliotecaForm(
                idUsuario,
                idJuego,
                entidad.getFechaAdquisicion(),
                entidad.getHorasDeJuego(),
                entidad.getUltimaFechaDeJuego(),
                true
        );

        bibliotecaRepo.actualizar(entidad.getIdBiblioteca(), form);

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
    // 7️⃣ ESTADÍSTICAS BIBLIOTECA
    // ======================================================

    @Test
    void estadisticasBiblioteca_calculoCompleto() throws Exception {

        controlador.anadirJuego(idUsuario, idJuego);

        var entidad = bibliotecaRepo.buscarPorUsuarioYJuego(idUsuario, idJuego);

        BibliotecaForm form = new BibliotecaForm(
                idUsuario,
                idJuego,
                entidad.getFechaAdquisicion(),
                5.0,
                entidad.getUltimaFechaDeJuego(),
                true
        );

        bibliotecaRepo.actualizar(entidad.getIdBiblioteca(), form);

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
