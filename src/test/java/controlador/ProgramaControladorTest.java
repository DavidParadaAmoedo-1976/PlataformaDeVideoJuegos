package controlador;

import org.davidparada.controlador.ProgramaControlador;
import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.JuegosPopularesDto;
import org.davidparada.modelo.dto.ReporteUsuariosDto;
import org.davidparada.modelo.dto.ReporteVentasDto;
import org.davidparada.modelo.enums.*;
import org.davidparada.modelo.formulario.*;
import org.davidparada.repositorio.implementacionMemoria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProgramaControladorTest {

    private ProgramaControlador controlador;

    private UsuarioRepo usuarioRepo;
    private JuegoRepo juegoRepo;
    private CompraRepo compraRepo;
    private BibliotecaRepo bibliotecaRepo;
    private ResenaRepo resenaRepo;

    private Instant ahora;
    @BeforeEach
    void setUp() {

        ahora = Instant.now();   // ← AÑADE ESTO

        usuarioRepo = new UsuarioRepo();
        juegoRepo = new JuegoRepo();
        compraRepo = new CompraRepo();
        bibliotecaRepo = new BibliotecaRepo();
        resenaRepo = new ResenaRepo();

        controlador = new ProgramaControlador(
                compraRepo,
                juegoRepo,
                usuarioRepo,
                bibliotecaRepo,
                resenaRepo
        );

        cargarDatosBase();
    }

    private void cargarDatosBase() {


        // =========================
        // USUARIOS
        // =========================

        usuarioRepo.crear(new UsuarioForm(
                "user1",
                "u1@test.com",
                "123",
                "User One",
                PaisEnum.ESPANA,
                LocalDate.of(2000,1,1),
                ahora.minus(10, ChronoUnit.DAYS),
                "avatar",
                100.0,
                EstadoCuentaEnum.ACTIVA
        ));

        usuarioRepo.crear(new UsuarioForm(
                "user2",
                "u2@test.com",
                "123",
                "User Two",
                PaisEnum.ESPANA,
                LocalDate.of(2000,1,1),
                ahora.minus(2, ChronoUnit.DAYS),
                "avatar",
                100.0,
                EstadoCuentaEnum.ACTIVA
        ));

        // Ajustar fechas para el test
        usuarioRepo.actualizar(1L, new UsuarioForm(
                "user1",
                "u1@test.com",
                "123",
                "User One",
                PaisEnum.ESPANA,
                LocalDate.of(2000,1,1),
                ahora.minus(10, ChronoUnit.DAYS),
                "avatar",
                100.0,
                EstadoCuentaEnum.ACTIVA
        ));

        usuarioRepo.actualizar(2L, new UsuarioForm(
                "user2",
                "u2@test.com",
                "123",
                "User Two",
                PaisEnum.ESPANA,
                LocalDate.of(2000,1,1),
                ahora.minus(2, ChronoUnit.DAYS),
                "avatar",
                100.0,
                EstadoCuentaEnum.ACTIVA
        ));

        // =========================
        // JUEGOS
        // =========================

        juegoRepo.crear(new JuegoForm(
                "Juego A",
                "desc",
                "Dev1",
                LocalDate.now(),
                50.0,
                0,
                "RPG",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"ES"},
                EstadoJuegoEnum.DISPONIBLE
        ));

        juegoRepo.crear(new JuegoForm(
                "Juego B",
                "desc",
                "Dev2",
                LocalDate.now(),
                40.0,
                10,
                "RPG",
                ClasificacionJuegoEnum.PEGI_12,
                new String[]{"ES"},
                EstadoJuegoEnum.DISPONIBLE
        ));

        // =========================
        // COMPRAS
        // =========================

        // =========================
// COMPRAS
// =========================

// Crear compras (descuento = 0 inicialmente)

        compraRepo.crear(new CompraForm(
                1L,
                1L,
                ahora.minus(5, ChronoUnit.DAYS),
                MetodoPagoEnum.TARJETA,
                50.0,
                50.0,
                EstadoCompraEnum.PENDIENTE
        ));

        compraRepo.crear(new CompraForm(
                2L,
                1L,
                ahora.minus(3, ChronoUnit.DAYS),
                MetodoPagoEnum.TARJETA,
                50.0,
                50.0,
                EstadoCompraEnum.PENDIENTE
        ));

        compraRepo.crear(new CompraForm(
                1L,
                2L,
                ahora.minus(1, ChronoUnit.DAYS),
                MetodoPagoEnum.TARJETA,
                40.0,
                36.0,
                EstadoCompraEnum.PENDIENTE
        ));

// Ahora actualizar para calcular descuento real

        compraRepo.actualizar(1L, new CompraForm(
                1L,
                1L,
                ahora.minus(5, ChronoUnit.DAYS),
                MetodoPagoEnum.TARJETA,
                50.0,
                50.0,
                EstadoCompraEnum.COMPLETADA
        ));

        compraRepo.actualizar(2L, new CompraForm(
                2L,
                1L,
                ahora.minus(3, ChronoUnit.DAYS),
                MetodoPagoEnum.TARJETA,
                50.0,
                50.0,
                EstadoCompraEnum.COMPLETADA
        ));

        compraRepo.actualizar(3L, new CompraForm(
                1L,
                2L,
                ahora.minus(1, ChronoUnit.DAYS),
                MetodoPagoEnum.TARJETA,
                40.0,
                36.0,
                EstadoCompraEnum.COMPLETADA
        ));

        // =========================
        // BIBLIOTECA
        // =========================

        bibliotecaRepo.crear(new BibliotecaForm(1L,1L,ahora,0.0,null,false));
        bibliotecaRepo.crear(new BibliotecaForm(2L,2L,ahora,0.0,null,false));

        bibliotecaRepo.actualizar(1L,
                new BibliotecaForm(1L,1L,ahora,20.0,ahora,true));

        bibliotecaRepo.actualizar(2L,
                new BibliotecaForm(2L,2L,ahora,50.0,ahora,true));

        // =========================
        // RESEÑAS
        // =========================

        resenaRepo.crear(new ResenaForm(
                1L,
                1L,
                true,
                "Muy bueno",
                10.0,
                ahora,
                ahora,
                EstadoPublicacionEnum.PUBLICADA
        ));

        resenaRepo.crear(new ResenaForm(
                2L,
                1L,
                false,
                "Regular",
                5.0,
                ahora,
                ahora,
                EstadoPublicacionEnum.PUBLICADA
        ));
    }

    // =========================
    // REPORTE VENTAS
    // =========================

    @Test
    void generarReporteVentas_debeCalcularTotalesCorrectamente() throws Exception {

        Instant inicio = Instant.now().minus(7, ChronoUnit.DAYS);
        Instant fin = Instant.now();

        ReporteVentasDto reporte =
                controlador.generarReporteVentas(inicio, fin, null, null);

        assertEquals(3, reporte.getTotalVentas());
        assertEquals(136.0, reporte.getIngresosTotales());
    }

    @Test
    void generarReporteVentas_filtrarPorJuego() throws Exception {

        Instant inicio = Instant.now().minus(7, ChronoUnit.DAYS);
        Instant fin = Instant.now();

        ReporteVentasDto reporte =
                controlador.generarReporteVentas(inicio, fin, 1L, null);

        assertEquals(2, reporte.getTotalVentas());
        assertEquals(100.0, reporte.getIngresosTotales());
    }

    @Test
    void generarReporteVentas_filtrarPorDesarrollador() throws Exception {

        Instant inicio = Instant.now().minus(7, ChronoUnit.DAYS);
        Instant fin = Instant.now();

        ReporteVentasDto reporte =
                controlador.generarReporteVentas(inicio, fin, null, "Dev2");

        assertEquals(1, reporte.getTotalVentas());
        assertEquals(36.0, reporte.getIngresosTotales());
    }

    @Test
    void generarReporteVentas_debeLanzarExcepcionSiRangoInvalido() {

        Instant inicio = Instant.now();
        Instant fin = Instant.now().minus(1, ChronoUnit.DAYS);

        assertThrows(
                ValidationException.class,
                () -> controlador.generarReporteVentas(inicio, fin, null, null)
        );
    }

    // =========================
    // REPORTE USUARIOS
    // =========================

    @Test
    void generarReporteUsuarios_debeCalcularNuevosYActivos() throws Exception {

        Instant inicio = ahora.minus(7, ChronoUnit.DAYS);
        Instant fin = ahora;

        ReporteUsuariosDto reporte =
                controlador.generarReporteUsuarios(inicio, fin);

        assertEquals(1, reporte.getNuevosUsuarios());
        assertEquals(2, reporte.getUsuariosActivos());
    }

    @Test
    void generarReporteUsuarios_debeLanzarExcepcionSiRangoInvalido() {

        Instant inicio = Instant.now();
        Instant fin = Instant.now().minus(1, ChronoUnit.DAYS);

        assertThrows(
                ValidationException.class,
                () -> controlador.generarReporteUsuarios(inicio, fin)
        );
    }

    // =========================
    // POPULARES
    // =========================

    @Test
    void consultarMasJugados_debeOrdenarYAsignarPosicionesCorrectamente() throws Exception {

        List<JuegosPopularesDto> lista =
                controlador.consultarJuegosMasPopulares(
                        CriterioPopularidadEnum.MAS_JUGADOS,
                        2
                );

        assertEquals(2, lista.size());

        assertEquals(1, lista.get(0).getPosicion());
        assertEquals(2L, lista.get(0).getJuego().idJuego());
        assertEquals(50.0, lista.get(0).getMetricaPrincipal());

        assertEquals(2, lista.get(1).getPosicion());
        assertEquals(1L, lista.get(1).getJuego().idJuego());
        assertEquals(20.0, lista.get(1).getMetricaPrincipal());
    }

    @Test
    void consultarMasVendidos_debeOrdenarCorrectamente() throws Exception {

        List<JuegosPopularesDto> lista =
                controlador.consultarJuegosMasPopulares(
                        CriterioPopularidadEnum.MAS_VENDIDOS,
                        2
                );

        assertEquals(2, lista.size());
        assertEquals(1L, lista.get(0).getJuego().idJuego());
        assertEquals(2.0, lista.get(0).getMetricaPrincipal());
    }

    @Test
    void consultarMejorValorados_debeCalcularMedia() throws Exception {

        List<JuegosPopularesDto> lista =
                controlador.consultarJuegosMasPopulares(
                        CriterioPopularidadEnum.MEJOR_VALORADOS,
                        1
                );

        assertEquals(1, lista.size());
        assertEquals(1L, lista.get(0).getJuego().idJuego());
        assertEquals(0.5, lista.get(0).getMetricaPrincipal());
    }

    @Test
    void consultarJuegosMasPopulares_debeLanzarExcepcionSiCriterioNull() {

        assertThrows(
                ValidationException.class,
                () -> controlador.consultarJuegosMasPopulares(null, 2)
        );
    }

    @Test
    void consultarJuegosMasPopulares_debeLanzarExcepcionSiLimiteInvalido() {

        assertThrows(
                ValidationException.class,
                () -> controlador.consultarJuegosMasPopulares(
                        CriterioPopularidadEnum.MAS_VENDIDOS,
                        0
                )
        );
    }


}