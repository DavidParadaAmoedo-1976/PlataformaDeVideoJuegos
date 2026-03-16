package controlador;

import org.davidparada.controlador.JuegoControlador;
import org.davidparada.controlador.util.ObtenerEntidadesOptional;
import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.JuegoDto;
import org.davidparada.modelo.enums.ClasificacionJuegoEnum;
import org.davidparada.modelo.enums.EstadoJuegoEnum;
import org.davidparada.modelo.enums.OrdenarJuegosEnum;
import org.davidparada.modelo.formulario.JuegoForm;
import org.davidparada.modelo.formulario.validacion.JuegoFormValidador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.davidparada.repositorio.implementacionMemoria.JuegoRepo;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JuegoControladorTest {

    private JuegoControlador juegoControlador;

    @BeforeEach
    void setUp() {
        JuegoRepo juegoRepo = new JuegoRepo();
        JuegoFormValidador.setJuegoRepo(juegoRepo);
        juegoControlador = new JuegoControlador(juegoRepo);

        new ObtenerEntidadesOptional(null, null, juegoRepo, null, null);

    }

    @Test
    void crearJuegoCorrectamente() throws ValidationException {

        JuegoForm form = new JuegoForm(
                "Juego Test",
                "Descripcion",
                "Dev Studio",
                LocalDate.of(2020,1,1),
                49.99,
                0,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );

        JuegoDto juego = juegoControlador.crearJuego(form);

        assertNotNull(juego);
        assertEquals("Juego Test", juego.titulo());
    }

    @Test
    void noPermiteTituloDuplicado() throws ValidationException {

        JuegoDto juego = crearJuegoBase();

        JuegoForm duplicado = new JuegoForm(
                juego.titulo(),   // mismo título
                "desc",
                "dev",
                LocalDate.now(),
                50.0,
                0,
                "accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"ES"},
                EstadoJuegoEnum.DISPONIBLE
        );

        assertThrows(
                ValidationException.class,
                () -> juegoControlador.crearJuego(duplicado)
        );
    }


    // Consultar catálogo
    @Test
    void catalogoVacio() throws ValidationException {

        List<JuegoDto> lista = juegoControlador.consultarCatalogo(null);

        assertTrue(lista.isEmpty());
    }

    @Test
    void consultarCatalogoOrdenadoAlfabeticamente() throws ValidationException {

        crearJuego("Zelda", 50.0, LocalDate.of(2020,1,1));
        crearJuego("Mario", 40.0, LocalDate.of(2019,1,1));
        crearJuego("Among Us", 10.0, LocalDate.of(2021,1,1));

        List<JuegoDto> lista = juegoControlador.consultarCatalogo(OrdenarJuegosEnum.ALFABETICO);

        assertEquals("Among Us", lista.get(0).titulo());
        assertEquals("Mario", lista.get(1).titulo());
        assertEquals("Zelda", lista.get(2).titulo());
    }
    @Test
    void consultarCatalogoOrdenadoPorPrecio() throws ValidationException {

        crearJuego("Juego A", 50.0, LocalDate.of(2020,1,1));
        crearJuego("Juego B", 10.0, LocalDate.of(2020,1,1));
        crearJuego("Juego C", 30.0, LocalDate.of(2020,1,1));

        List<JuegoDto> lista = juegoControlador.consultarCatalogo(OrdenarJuegosEnum.PRECIO);

        assertEquals(10.0, lista.get(0).precioBase());
        assertEquals(30.0, lista.get(1).precioBase());
        assertEquals(50.0, lista.get(2).precioBase());
    }
    @Test
    void consultarCatalogoOrdenadoPorFecha() throws ValidationException {

        crearJuego("Juego A", 50.0, LocalDate.of(2022,1,1));
        crearJuego("Juego B", 50.0, LocalDate.of(2018,1,1));
        crearJuego("Juego C", 50.0, LocalDate.of(2020,1,1));

        List<JuegoDto> lista = juegoControlador.consultarCatalogo(OrdenarJuegosEnum.FECHA);

        assertEquals(LocalDate.of(2018,1,1), lista.get(0).fechaLanzamiento());
        assertEquals(LocalDate.of(2020,1,1), lista.get(1).fechaLanzamiento());
        assertEquals(LocalDate.of(2022,1,1), lista.get(2).fechaLanzamiento());
    }
    @Test
    void consultarCatalogoSinOrdenDevuelveLista() throws ValidationException {

        crearJuego("Juego A", 50.0, LocalDate.of(2020,1,1));
        crearJuego("Juego B", 40.0, LocalDate.of(2021,1,1));

        List<JuegoDto> lista = juegoControlador.consultarCatalogo(null);

        assertEquals(2, lista.size());
    }

    // Consultar detalles

    @Test
    void consultarDetallesDevuelveJuegoCorrecto() throws ValidationException {
        JuegoDto juego = crearJuegoBase();

        JuegoDto consultado = juegoControlador.consultarDetalles(juego.idJuego());

        assertEquals(juego.idJuego(), consultado.idJuego());
    }

    @Test
    void consultarDetallesFallaSiNoExiste() {
        assertThrows(ValidationException.class, () -> {
            juegoControlador.consultarDetalles(999L);
        });
    }


    // Aplicar descuento

    @Test
    void aplicarDescuentoCorrectamente() throws ValidationException {

        JuegoForm form = new JuegoForm(
                "Juego Descuento",
                "Descripcion",
                "Dev Studio",
                LocalDate.of(2020,1,1),
                100.0,
                0,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );

        JuegoDto juego = juegoControlador.crearJuego(form);

        // Aplico descuento
        juegoControlador.aplicarDescuento(
                juego.idJuego(),
                20
        );

        // Busco el juego nuevamente
        JuegoDto actualizado = juegoControlador.consultarDetalles(juego.idJuego());

        assertEquals(20, actualizado.descuento());
    }

    @Test
    void aplicarDescuentoFallaSiDescuentoEsNull() throws ValidationException {
        JuegoDto juego = crearJuegoBase();

        assertThrows(ValidationException.class, () -> {
            juegoControlador.aplicarDescuento(juego.idJuego(), null);
        });
    }

    @Test
    void aplicarDescuentoFallaSiDescuentoEsNegativo() throws ValidationException {
        JuegoDto juego = crearJuegoBase();

        assertThrows(ValidationException.class, () -> {
            juegoControlador.aplicarDescuento(juego.idJuego(), -5);
        });
    }

    @Test
    void aplicarDescuentoFallaSiDescuentoMayorA100() throws ValidationException {
        JuegoDto juego = crearJuegoBase();

        assertThrows(ValidationException.class, () -> {
            juegoControlador.aplicarDescuento(juego.idJuego(), 150);
        });
    }

    @Test
    void aplicarDescuentoFallaSiJuegoNoExiste() {
        assertThrows(ValidationException.class, () -> {
            juegoControlador.aplicarDescuento(999L, 20);
        });
    }

    @Test
    void aplicarDescuentoNoModificaOtrosCampos() throws ValidationException {
        JuegoDto juego = crearJuegoBase();

        juegoControlador.aplicarDescuento(juego.idJuego(), 30);

        JuegoDto actualizado = juegoControlador.consultarDetalles(juego.idJuego());

        assertEquals(juego.titulo(), actualizado.titulo());
        assertEquals(juego.precioBase(), actualizado.precioBase());
        assertEquals(30, actualizado.descuento());
    }

    // Cambiar estado del juego

    @Test
    void cambiarEstadoCorrectamente() throws ValidationException {
        JuegoDto juego = crearJuegoBase();

        juegoControlador.cambiarEstado(
                juego.idJuego(),
                EstadoJuegoEnum.NO_DISPONIBLE
        );

        JuegoDto actualizado = juegoControlador.consultarDetalles(juego.idJuego());

        assertEquals(EstadoJuegoEnum.NO_DISPONIBLE, actualizado.estado());
    }

    @Test
    void cambiarEstadoFallaSiIdEsNull() {
        assertThrows(ValidationException.class, () -> {
            juegoControlador.cambiarEstado(null, EstadoJuegoEnum.NO_DISPONIBLE);
        });
    }

    @Test
    void cambiarEstadoFallaSiEstadoEsNull() throws ValidationException {
        JuegoDto juego = crearJuegoBase();

        assertThrows(ValidationException.class, () -> {
            juegoControlador.cambiarEstado(juego.idJuego(), null);
        });
    }

    @Test
    void cambiarEstadoNoModificaOtrosCampos() throws ValidationException {
        JuegoDto juego = crearJuegoBase();

        juegoControlador.cambiarEstado(
                juego.idJuego(),
                EstadoJuegoEnum.NO_DISPONIBLE
        );

        JuegoDto actualizado = juegoControlador.consultarDetalles(juego.idJuego());

        assertEquals(juego.titulo(), actualizado.titulo());
        assertEquals(juego.precioBase(), actualizado.precioBase());
    }

    @Test
    void cambiarEstadoFallaSiJuegoNoExiste() {

        assertThrows(ValidationException.class, () -> {
            juegoControlador.cambiarEstado(
                    999L,
                    EstadoJuegoEnum.NO_DISPONIBLE
            );
        });
    }

    @Test
    void crearJuegoFallaSiFormularioEsNull() {

        assertThrows(
                ValidationException.class,
                () -> juegoControlador.crearJuego(null)
        );
    }

    @Test
    void consultarDetallesFallaSiIdEsNull() {

        assertThrows(
                ValidationException.class,
                () -> juegoControlador.consultarDetalles(null)
        );
    }

    // Fallos test del profesor

// ===============================
// VALIDACIONES FORMULARIO JUEGO
// ===============================

    @Test
    void crearJuego_fallaSiTituloNull() {

        JuegoForm form = new JuegoForm(
                null,
                "Descripcion valida",
                "Dev Studio",
                LocalDate.of(2020,1,1),
                50.0,
                0,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );

        assertThrows(ValidationException.class,
                () -> juegoControlador.crearJuego(form));
    }

    @Test
    void crearJuego_fallaSiTituloMayor100() {

        JuegoForm form = new JuegoForm(
                "A".repeat(101),
                "Descripcion valida",
                "Dev Studio",
                LocalDate.of(2020,1,1),
                50.0,
                0,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );

        assertThrows(ValidationException.class,
                () -> juegoControlador.crearJuego(form));
    }

    @Test
    void crearJuego_fallaSiTituloDuplicado() throws ValidationException {

        JuegoForm form = crearFormularioValido();
        juegoControlador.crearJuego(form);

        JuegoForm duplicado = crearFormularioValido();

        assertThrows(ValidationException.class,
                () -> juegoControlador.crearJuego(duplicado));
    }

    @Test
    void crearJuego_fallaSiDescripcionMayor2000() {

        JuegoForm form = new JuegoForm(
                "Juego Test",
                "A".repeat(2001),
                "Dev Studio",
                LocalDate.of(2020,1,1),
                50.0,
                0,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );

        assertThrows(ValidationException.class,
                () -> juegoControlador.crearJuego(form));
    }

    @Test
    void crearJuego_fallaSiDesarrolladorNull() {

        JuegoForm form = new JuegoForm(
                "Juego Test",
                "Descripcion valida",
                null,
                LocalDate.of(2020,1,1),
                50.0,
                0,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );

        assertThrows(ValidationException.class,
                () -> juegoControlador.crearJuego(form));
    }

    @Test
    void crearJuego_fallaSiDesarrolladorMuyCorto() {

        JuegoForm form = new JuegoForm(
                "Juego Test",
                "Descripcion valida",
                "A",
                LocalDate.of(2020,1,1),
                50.0,
                0,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );

        assertThrows(ValidationException.class,
                () -> juegoControlador.crearJuego(form));
    }

    @Test
    void crearJuego_fallaSiDesarrolladorMuyLargo() {

        JuegoForm form = new JuegoForm(
                "Juego Test",
                "Descripcion valida",
                "A".repeat(101),
                LocalDate.of(2020,1,1),
                50.0,
                0,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );

        assertThrows(ValidationException.class,
                () -> juegoControlador.crearJuego(form));
    }

    @Test
    void crearJuego_fallaSiFechaNull() {

        JuegoForm form = new JuegoForm(
                "Juego Test",
                "Descripcion valida",
                "Dev Studio",
                null,
                50.0,
                0,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );

        assertThrows(ValidationException.class,
                () -> juegoControlador.crearJuego(form));
    }

    @Test
    void crearJuego_fallaSiPrecioNegativo() {

        JuegoForm form = new JuegoForm(
                "Juego Test",
                "Descripcion valida",
                "Dev Studio",
                LocalDate.of(2020,1,1),
                -10.0,
                0,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );

        assertThrows(ValidationException.class,
                () -> juegoControlador.crearJuego(form));
    }

    @Test
    void crearJuego_fallaSiPrecioMayorMaximo() {

        JuegoForm form = new JuegoForm(
                "Juego Test",
                "Descripcion valida",
                "Dev Studio",
                LocalDate.of(2020,1,1),
                1000.0,
                0,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );

        assertThrows(ValidationException.class,
                () -> juegoControlador.crearJuego(form));
    }

    @Test
    void crearJuego_fallaSiDescuentoNegativo() {

        JuegoForm form = new JuegoForm(
                "Juego Test",
                "Descripcion valida",
                "Dev Studio",
                LocalDate.of(2020,1,1),
                50.0,
                -5,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );

        assertThrows(ValidationException.class,
                () -> juegoControlador.crearJuego(form));
    }

    @Test
    void crearJuego_fallaSiDescuentoMayor100() {

        JuegoForm form = new JuegoForm(
                "Juego Test",
                "Descripcion valida",
                "Dev Studio",
                LocalDate.of(2020,1,1),
                50.0,
                150,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );

        assertThrows(ValidationException.class,
                () -> juegoControlador.crearJuego(form));
    }

    @Test
    void crearJuego_fallaSiClasificacionNull() {

        JuegoForm form = new JuegoForm(
                "Juego Test",
                "Descripcion valida",
                "Dev Studio",
                LocalDate.of(2020,1,1),
                50.0,
                0,
                "Accion",
                null,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );

        assertThrows(ValidationException.class,
                () -> juegoControlador.crearJuego(form));
    }

    @Test
    void crearJuego_fallaSiIdiomasVacio() {

        JuegoForm form = new JuegoForm(
                "Juego Test",
                "Descripcion valida",
                "Dev Studio",
                LocalDate.of(2020,1,1),
                50.0,
                0,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{},
                EstadoJuegoEnum.DISPONIBLE
        );

        assertThrows(ValidationException.class,
                () -> juegoControlador.crearJuego(form));
    }


// ===============================
// HELPER
// ===============================

    private static int contadorJuegos = 0;

    private JuegoDto crearJuegoBase() throws ValidationException {

        contadorJuegos++;

        JuegoForm form = new JuegoForm(
                "Juego Base_" + contadorJuegos,
                "Descripcion Base " + contadorJuegos,
                "Dev Studio",
                LocalDate.of(2020, 1, 1),
                100.0 + contadorJuegos,
                0,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );

        return juegoControlador.crearJuego(form);
    }

    private void crearJuego(String titulo, Double precio, LocalDate fecha) throws ValidationException {

        JuegoForm form = new JuegoForm(
                titulo,
                "Descripcion",
                "Dev Studio",
                fecha,
                precio,
                0,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );

        juegoControlador.crearJuego(form);
    }

    private JuegoForm crearFormularioValido() {
        return new JuegoForm(
                "Juego Test",
                "Descripcion correcta",
                "Dev Studio",
                LocalDate.of(2020,1,1),
                50.0,
                0,
                "Accion",
                ClasificacionJuegoEnum.PEGI_18,
                new String[]{"Espanol"},
                EstadoJuegoEnum.DISPONIBLE
        );
    }

}

