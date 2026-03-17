package controlador;

import org.davidparada.controlador.BibliotecaControlador;
import org.davidparada.controlador.CompraControlador;
import org.davidparada.controlador.util.ObtenerEntidadesOptional;
import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.CompraDto;
import org.davidparada.modelo.dto.DetallesCompraDto;
import org.davidparada.modelo.dto.FacturaDto;
import org.davidparada.modelo.entidad.CompraEntidad;
import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.enums.*;
import org.davidparada.modelo.formulario.CompraForm;
import org.davidparada.modelo.formulario.JuegoForm;
import org.davidparada.modelo.formulario.UsuarioForm;
import org.davidparada.repositorio.implementacionMemoria.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompraControladorTest {

    private CompraControlador compraControlador;
    private UsuarioRepo usuarioRepo;
    private JuegoRepo juegoRepo;
    private CompraRepo compraRepo;
    private BibliotecaRepo bibliotecaRepo;
    private BibliotecaControlador bibliotecaControlador;

    @BeforeEach
    void setup() {
        usuarioRepo = new UsuarioRepo();
        juegoRepo = new JuegoRepo();
        compraRepo = new CompraRepo();
        bibliotecaRepo = new BibliotecaRepo();
        bibliotecaControlador = new BibliotecaControlador(bibliotecaRepo, juegoRepo);

        compraControlador = new CompraControlador(
                compraRepo,
                usuarioRepo,
                juegoRepo,
                bibliotecaRepo,
                bibliotecaControlador
        );

        new ObtenerEntidadesOptional(compraRepo, usuarioRepo, juegoRepo, bibliotecaRepo, null);
    }

// =========================
// REALIZAR COMPRA
// =========================

    @Test
    void realizarCompra_ok() throws Exception {

        var usuario = crearUsuario();
        var juego = crearJuego();

        CompraDto dto =
                compraControlador.realizarCompra(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        MetodoPagoEnum.TARJETA
                );

        assertNotNull(dto);
        assertEquals(usuario.getIdUsuario(), dto.idUsuario());
    }

    @Test
    void realizarCompra_DescuentoNegativo_LanzaValidationException() throws Exception {

        var usuario = crearUsuario();

        var juego = juegoRepo.crear(
                new JuegoForm(
                        "Juego",
                        "Desc",
                        "Dev",
                        LocalDate.now(),
                        50.0,
                        -10,
                        "Accion",
                        ClasificacionJuegoEnum.PEGI_18,
                        new String[]{"ES"},
                        EstadoJuegoEnum.DISPONIBLE
                )
        );

        assertThrows(
                ValidationException.class,
                () -> compraControlador.realizarCompra(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        MetodoPagoEnum.PAYPAL
                )
        );
    }

    @Test
    void realizarCompra_DescuentoMayor100_LanzaValidationException() throws Exception {

        var usuario = crearUsuario();

        var juego = juegoRepo.crear(
                new JuegoForm(
                        "Juego",
                        "Desc",
                        "Dev",
                        LocalDate.now(),
                        50.0,
                        120,
                        "Accion",
                        ClasificacionJuegoEnum.PEGI_18,
                        new String[]{"ES"},
                        EstadoJuegoEnum.DISPONIBLE
                )
        );

        assertThrows(
                ValidationException.class,
                () -> compraControlador.realizarCompra(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        MetodoPagoEnum.PAYPAL
                )
        );
    }

    @Test
    void usuarioSuspendidoNoPuedeComprar() throws Exception {

        var usuario = usuarioRepo.crear(new UsuarioForm(
                "user","email@test.com","Password1","Nombre",
                PaisEnum.ESPANA,
                LocalDate.now().minusYears(20),
                Instant.now(),
                null,
                100.0,
                EstadoCuentaEnum.ACTIVA
        ));
                usuarioRepo.actualizar(usuario.getIdUsuario(), new UsuarioForm(
                usuario.getNombreUsuario(),
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getNombreReal(),
                usuario.getPais(),
                usuario.getFechaNacimiento(),
                usuario.getFechaRegistro(),
                usuario.getAvatar(),
                usuario.getSaldo(),
                EstadoCuentaEnum.SUSPENDIDA
        ));
        var juego = crearJuego();

        assertThrows(
                ValidationException.class,
                () -> compraControlador.realizarCompra(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        MetodoPagoEnum.PAYPAL
                )
        );
    }

    @Test
    void noPermiteComprarJuegoDosVeces() throws Exception {

        var usuario = crearUsuario();
        var juego = crearJuego();

        compraControlador.realizarCompra(
                usuario.getIdUsuario(),
                juego.getIdJuego(),
                MetodoPagoEnum.PAYPAL
        );

        assertThrows(
                ValidationException.class,
                () -> compraControlador.realizarCompra(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        MetodoPagoEnum.PAYPAL
                )
        );
    }

    @Test
    void realizarCompra_usuarioNoExiste() {

        assertThrows(
                ValidationException.class,
                () -> compraControlador.realizarCompra(
                        1L,
                        1L,
                        MetodoPagoEnum.PAYPAL
                )
        );
    }

    @Test
    void realizarCompra_juegoNoDisponible() throws Exception {

        var usuario = crearUsuario();
        var juego = crearJuego();

        juegoRepo.actualizar(
                juego.getIdJuego(),
                new JuegoForm(
                        juego.getTitulo(),
                        juego.getDescripcion(),
                        juego.getDesarrollador(),
                        juego.getFechaLanzamiento(),
                        juego.getPrecioBase(),
                        juego.getDescuento(),
                        juego.getCategoria(),
                        juego.getClasificacionPorEdad(),
                        juego.getIdiomas(),
                        EstadoJuegoEnum.NO_DISPONIBLE
                )
        );

        assertThrows(
                ValidationException.class,
                () -> compraControlador.realizarCompra(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        MetodoPagoEnum.PAYPAL
                )
        );
    }

// =========================
// PROCESAR PAGO
// =========================

    @Test
    void procesarPago_CompraEnEstadoPendiente_RetornaCompraCompletada() throws Exception {

        var usuario = crearUsuario();
        var juego = crearJuego();

        var compra = compraRepo.crear(
                new CompraForm(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        Instant.now(),
                        MetodoPagoEnum.TARJETA,
                        50.0,
                        50.0,
                        EstadoCompraEnum.PENDIENTE
                )
        );

        compraControlador.procesarPago(
                compra.getIdCompra(),
                MetodoPagoEnum.TARJETA
        );

        var actualizada = compraRepo.buscarPorId(compra.getIdCompra()).get();

        assertEquals(
                EstadoCompraEnum.COMPLETADA,
                actualizada.getEstadoCompra()
        );
    }

    @Test
    void realizarCompra_MetodoPagoNulo_LanzaValidationException() throws Exception {

        var usuario = crearUsuario();
        var juego = crearJuego();

        assertThrows(
                ValidationException.class,
                () -> compraControlador.realizarCompra(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        null
                )
        );
    }

    @Test
    void realizarCompra_PrecioSinDescuentoNegativo_LanzaValidationException() throws Exception {

        var usuario = crearUsuario();

        var juego = juegoRepo.crear(
                new JuegoForm(
                        "Juego",
                        "Desc",
                        "Dev",
                        LocalDate.now(),
                        -10.0,
                        0,
                        "Accion",
                        ClasificacionJuegoEnum.PEGI_18,
                        new String[]{"ES"},
                        EstadoJuegoEnum.DISPONIBLE
                )
        );

        assertThrows(
                ValidationException.class,
                () -> compraControlador.realizarCompra(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        MetodoPagoEnum.PAYPAL
                )
        );
    }

    @Test
    void procesarPago_CompraYaCompletada_LanzaValidationException() throws Exception {

        var usuario = crearUsuario();
        var juego = crearJuego();

        var compra = compraRepo.crear(
                new CompraForm(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        Instant.now(),
                        MetodoPagoEnum.PAYPAL,
                        50.0,
                        50.0,
                        EstadoCompraEnum.COMPLETADA
                )
        );

        assertThrows(
                ValidationException.class,
                () -> compraControlador.procesarPago(
                        compra.getIdCompra(),
                        MetodoPagoEnum.PAYPAL
                )
        );
    }

    @Test
    void procesarPago_IdInvalido_LanzaValidationException() {

        assertThrows(
                ValidationException.class,
                () -> compraControlador.procesarPago(
                        null,
                        MetodoPagoEnum.PAYPAL
                )
        );
    }

    @Test
    void procesarPagoCompraNoExiste() {

        assertThrows(
                ValidationException.class,
                () -> compraControlador.procesarPago(
                        999L,
                        MetodoPagoEnum.PAYPAL
                )
        );
    }

    @Test
    void realizarCompra_DescuentoCero_PorDefecto() throws Exception {

        var usuario = crearUsuario();
        var juego = crearJuego();

        CompraDto dto =
                compraControlador.realizarCompra(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        MetodoPagoEnum.PAYPAL
                );

        assertEquals(juego.getPrecioBase(), dto.precioBase(), 0.001);
        assertEquals(0, dto.descuento());
    }

    @Test
    void realizarCompra_PrecioSinDescuentoCero_permitido() throws Exception {

        var usuario = crearUsuario();
        var juego = crearJuego();

        CompraDto dto =
                compraControlador.realizarCompra(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        MetodoPagoEnum.PAYPAL
                );

        assertNotNull(dto);
    }

// =========================
// CONSULTAR COMPRA
// =========================

    @Test
    void consultarCompra_IdValidoUsuarioCorrecto_RetornaCompraDTO() throws Exception {

        var usuario = crearUsuario();
        var juego = crearJuego();

        var compra = compraRepo.crear(
                new CompraForm(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        Instant.now(),
                        MetodoPagoEnum.PAYPAL,
                        50.0,
                        50.0,
                        EstadoCompraEnum.COMPLETADA
                )
        );

        CompraDto dto =
                compraControlador.consultarCompra(
                        compra.getIdCompra(),
                        usuario.getIdUsuario()
                );

        assertNotNull(dto);
        assertEquals(compra.getIdCompra(), dto.idCompra());
    }

    @Test
    void consultarCompra_IdInvalido_LanzaValidationException() {

        assertThrows(
                ValidationException.class,
                () -> compraControlador.consultarCompra(null, 1L)
        );
    }

    @Test
    void consultarCompra_UsuarioNoEsDuenio_LanzaValidationException() throws Exception {

        var usuario1 = crearUsuario();
        var usuario2 = crearUsuario();
        var juego = crearJuego();

        var compra = compraRepo.crear(
                new CompraForm(
                        usuario1.getIdUsuario(),
                        juego.getIdJuego(),
                        Instant.now(),
                        MetodoPagoEnum.PAYPAL,
                        50.0,
                        50.0,
                        EstadoCompraEnum.COMPLETADA
                )
        );

        assertThrows(
                ValidationException.class,
                () -> compraControlador.consultarCompra(
                        compra.getIdCompra(),
                        usuario2.getIdUsuario()
                )
        );
    }

// =========================
// LISTAR COMPRAS
// =========================

    @Test
    void listarCompras_ok() throws Exception {

        var usuario = crearUsuario();

        List<CompraDto> lista =
                compraControlador.listarCompras(usuario.getIdUsuario());

        assertNotNull(lista);
    }

// =========================
// DETALLES COMPRA
// =========================

    @Test
    void detallesCompra_ok() throws Exception {

        var usuario = crearUsuario();
        var juego = crearJuego();

        var compra = compraRepo.crear(
                new CompraForm(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        Instant.now(),
                        MetodoPagoEnum.PAYPAL,
                        50.0,
                        50.0,
                        EstadoCompraEnum.COMPLETADA
                )
        );

        DetallesCompraDto detalles =
                compraControlador.detallesDeUnaCompra(
                        compra.getIdCompra(),
                        usuario.getIdUsuario()
                );

        assertNotNull(detalles);
        assertNotNull(detalles.facturaDto());
    }

// =========================
// REEMBOLSO
// =========================

    @Test
    void solicitarReembolso_ok() throws Exception {

        var usuario = crearUsuario();
        var juego = crearJuego();

        var compra = compraRepo.crear(
                new CompraForm(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        Instant.now(),
                        MetodoPagoEnum.PAYPAL,
                        50.0,
                        50.0,
                        EstadoCompraEnum.PENDIENTE
                )
        );

        compraControlador.procesarPago(
                compra.getIdCompra(),
                MetodoPagoEnum.PAYPAL
        );

        compraControlador.solicitarReembolso(compra.getIdCompra());

        assertEquals(
                EstadoCompraEnum.REEMBOLSADA,
                compraRepo.buscarPorId(compra.getIdCompra()).get().getEstadoCompra()
        );
    }

    @Test
    void solicitarReembolso_CompraCompletada_RetornaCompraReembolsada() throws Exception {

        var usuario = crearUsuario();
        var juego = crearJuego();

        var compra = compraRepo.crear(
                new CompraForm(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        Instant.now(),
                        MetodoPagoEnum.PAYPAL,
                        50.0,
                        50.0,
                        EstadoCompraEnum.COMPLETADA
                )
        );

        compraControlador.solicitarReembolso(compra.getIdCompra());

        var actualizada =
                compraRepo.buscarPorId(compra.getIdCompra()).get();

        assertEquals(
                EstadoCompraEnum.REEMBOLSADA,
                actualizada.getEstadoCompra()
        );
    }

// =========================
// FACTURA
// =========================

    @Test
    void generarFactura_ok() throws Exception {

        var usuario = crearUsuario();
        var juego = crearJuego();

        var compra = compraRepo.crear(
                new CompraForm(
                        usuario.getIdUsuario(),
                        juego.getIdJuego(),
                        Instant.now(),
                        MetodoPagoEnum.PAYPAL,
                        50.0,
                        50.0,
                        EstadoCompraEnum.COMPLETADA
                )
        );

        FacturaDto factura =
                compraControlador.generarFactura(compra.getIdCompra());

        assertNotNull(factura);
        assertEquals(compra.getIdCompra(), factura.idCompra());
    }

// =========================
// HELPERS
// =========================

    private UsuarioEntidad crearUsuario() {

        return usuarioRepo.crear(
                new UsuarioForm(
                        "user" + System.nanoTime(),
                        "email" + System.nanoTime() + "@test.com",
                        "Password1",
                        "Nombre",
                        PaisEnum.ESPANA,
                        LocalDate.now().minusYears(20),
                        Instant.now(),
                        "avatar",
                        100.0,
                        EstadoCuentaEnum.ACTIVA
                )
        );
    }

    private JuegoEntidad crearJuego() {

        return juegoRepo.crear(
                new JuegoForm(
                        "Juego" + System.nanoTime(),
                        "Descripcion",
                        "Dev",
                        LocalDate.now(),
                        50.0,
                        0,
                        "Accion",
                        ClasificacionJuegoEnum.PEGI_18,
                        new String[]{"ES"},
                        EstadoJuegoEnum.DISPONIBLE
                )
        );
    }

    private CompraEntidad crearCompra(Long idUsuario, Long idJuego, EstadoCompraEnum estado) {

        return compraRepo.crear(
                new CompraForm(
                        idUsuario,
                        idJuego,
                        Instant.now(),
                        MetodoPagoEnum.PAYPAL,
                        50.0,
                        50.0,
                        estado
                )
        );
    }
}
