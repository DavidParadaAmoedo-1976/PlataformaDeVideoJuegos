package controlador;

import org.davidparada.controlador.JuegoControlador;
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

    // Consultar catálogo

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

        crearJuegoBase();
        crearJuegoBase();

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

        assertEquals("Juego Base", actualizado.titulo());
        assertEquals(100.0, actualizado.precioBase());
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

        assertEquals("Juego Base", actualizado.titulo());
        assertEquals(100.0, actualizado.precioBase());
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

    private JuegoDto crearJuegoBase() throws ValidationException {

        JuegoForm form = new JuegoForm(
                "Juego Base",
                "Descripcion Base",
                "Dev Studio",
                LocalDate.of(2020, 1, 1),
                100.0,
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

}

