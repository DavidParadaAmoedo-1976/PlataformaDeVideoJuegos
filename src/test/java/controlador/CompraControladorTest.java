package controlador;

import org.davidparada.controlador.BibliotecaControlador;
import org.davidparada.controlador.CompraControlador;
import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.CompraDto;
import org.davidparada.modelo.dto.DetallesCompraDto;
import org.davidparada.modelo.dto.FacturaDto;
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
    }

    // =========================
    // REALIZAR COMPRA
    // =========================

    @Test
    void realizarCompra_ok() throws Exception {

        var usuario = usuarioRepo.crear(new UsuarioForm(
                "user1","email@test.com","123","Nombre",
                PaisEnum.ESPANA, LocalDate.now().minusYears(20),
                Instant.now(),"avatar",100.0, EstadoCuentaEnum.ACTIVA));

        var juego = juegoRepo.crear(new JuegoForm(
                "Juego1","desc","dev",
                LocalDate.now(),50.0,0,"accion",
                ClasificacionJuegoEnum.PEGI_18,new String[]{"ES"},
                EstadoJuegoEnum.DISPONIBLE));

        Double precioFinal = juego.getPrecioBase() * (1- juego.getDescuento() / 100.0);
        CompraForm form = new CompraForm(
                usuario.getIdUsuario(),
                juego.getIdJuego(),
                Instant.now(),
                MetodoPagoEnum.TARJETA,
                juego.getPrecioBase(),
                precioFinal,
                EstadoCompraEnum.PENDIENTE
        );

        CompraDto dto = compraControlador.realizarCompra(form);

        assertNotNull(dto);
        assertEquals(usuario.getIdUsuario(), dto.idUsuario());
    }

    @Test
    void realizarCompra_usuarioNoExiste() {
        assertThrows(ValidationException.class, () -> {
            compraControlador.realizarCompra(
                    new CompraForm(1L,1L,Instant.now(),
                            MetodoPagoEnum.PAYPAL,10.0,50.0,
                            EstadoCompraEnum.PENDIENTE));
        });
    }

    @Test
    void realizarCompra_juegoNoDisponible() throws Exception {

        var usuario = usuarioRepo.crear(new UsuarioForm(
                "u","e","p","n",PaisEnum.ESPANA,
                LocalDate.now().minusYears(20),
                Instant.now(),"a",100.0,EstadoCuentaEnum.ACTIVA));

        var juego = juegoRepo.crear(new JuegoForm(
                "j","d","dev",
                LocalDate.now(),50.0,0,"accion",
                ClasificacionJuegoEnum.PEGI_18,new String[]{"ES"},
                EstadoJuegoEnum.DISPONIBLE));

        // ahora lo actualizamos a NO_DISPONIBLE
        juegoRepo.actualizar(juego.getIdJuego(),
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
                ));

        assertThrows(ValidationException.class, () ->
                compraControlador.realizarCompra(
                        new CompraForm(usuario.getIdUsuario(),
                                juego.getIdJuego(),
                                Instant.now(),
                                MetodoPagoEnum.PAYPAL,
                                50.0,50.0,
                                EstadoCompraEnum.PENDIENTE)
                )
        );
    }

    // =========================
    // PROCESAR PAGO
    // =========================

    @Test
    void procesarPago_ok_tarjeta() throws Exception {

        var usuario = usuarioRepo.crear(new UsuarioForm(
                "u","e","p","n",PaisEnum.ESPANA,
                LocalDate.now().minusYears(20),
                Instant.now(),"a",100.0,EstadoCuentaEnum.ACTIVA));

        var juego = juegoRepo.crear(new JuegoForm(
                "j","d","dev",
                LocalDate.now(),50.0,0,"accion",
                ClasificacionJuegoEnum.PEGI_18,new String[]{"ES"},
                EstadoJuegoEnum.DISPONIBLE));

        CompraForm form = new CompraForm(
                usuario.getIdUsuario(),
                juego.getIdJuego(),
                Instant.now(),
                MetodoPagoEnum.TARJETA,
                50.0,50.0,
                EstadoCompraEnum.PENDIENTE);

        var compra = compraRepo.crear(form);

        boolean resultado = compraControlador.procesarPago(
                compra.getIdCompra(),
                MetodoPagoEnum.TARJETA);

        assertTrue(resultado);
    }

    // =========================
    // LISTAR COMPRAS
    // =========================

    @Test
    void listarCompras_ok() throws Exception {

        var usuario = usuarioRepo.crear(new UsuarioForm(
                "u","e","p","n",PaisEnum.ESPANA,
                LocalDate.now().minusYears(20),
                Instant.now(),"a",100.0,EstadoCuentaEnum.ACTIVA));

        List<CompraDto> lista = compraControlador.listarCompras(usuario.getIdUsuario());

        assertNotNull(lista);
    }

    // =========================
    // DETALLES COMPRA
    // =========================

    @Test
    void detallesCompra_ok() throws Exception {

        var usuario = usuarioRepo.crear(new UsuarioForm(
                "u","e","p","n",PaisEnum.ESPANA,
                LocalDate.now().minusYears(20),
                Instant.now(),"a",100.0,EstadoCuentaEnum.ACTIVA));

        var juego = juegoRepo.crear(new JuegoForm(
                "j","d","dev",
                LocalDate.now(),50.0,0,"accion",
                ClasificacionJuegoEnum.PEGI_18,new String[]{"ES"},
                EstadoJuegoEnum.DISPONIBLE));

        var compra = compraRepo.crear(new CompraForm(
                usuario.getIdUsuario(),
                juego.getIdJuego(),
                Instant.now(),
                MetodoPagoEnum.PAYPAL,
                50.0,50.0,
                EstadoCompraEnum.COMPLETADA));

        DetallesCompraDto detalles = compraControlador.detallesDeUnaCompra(
                compra.getIdCompra(),
                usuario.getIdUsuario());

        assertNotNull(detalles);
        assertNotNull(detalles.facturaDto());
    }

    // =========================
    // REEMBOLSO
    // =========================

    @Test
    void solicitarReembolso_ok() throws Exception {

        var usuario = usuarioRepo.crear(new UsuarioForm(
                "u","e","p","n",PaisEnum.ESPANA,
                LocalDate.now().minusYears(20),
                Instant.now(),"a",100.0,EstadoCuentaEnum.ACTIVA));

        var juego = juegoRepo.crear(new JuegoForm(
                "j","d","dev",
                LocalDate.now(),50.0,0,"accion",
                ClasificacionJuegoEnum.PEGI_18,new String[]{"ES"},
                EstadoJuegoEnum.DISPONIBLE));

        var compra = compraRepo.crear(new CompraForm(
                usuario.getIdUsuario(),
                juego.getIdJuego(),
                Instant.now(),
                MetodoPagoEnum.PAYPAL,
                50.0,50.0,
                EstadoCompraEnum.PENDIENTE));

        compraControlador.procesarPago(
                compra.getIdCompra(),
                MetodoPagoEnum.PAYPAL);

        compraControlador.solicitarReembolso(compra.getIdCompra());

        assertEquals(
                EstadoCompraEnum.REEMBOLSADA,
                compraRepo.buscarPorId(compra.getIdCompra()).get().getEstadoCompra());
    }

    // =========================
    // FACTURA
    // =========================

    @Test
    void generarFactura_ok() throws Exception {

        var usuario = usuarioRepo.crear(new UsuarioForm(
                "u","e","p","n",PaisEnum.ESPANA,
                LocalDate.now().minusYears(20),
                Instant.now(),"a",100.0,EstadoCuentaEnum.ACTIVA));

        var juego = juegoRepo.crear(new JuegoForm(
                "j","d","dev",
                LocalDate.now(),50.0,0,"accion",
                ClasificacionJuegoEnum.PEGI_18,new String[]{"ES"},
                EstadoJuegoEnum.DISPONIBLE));

        var compra = compraRepo.crear(new CompraForm(
                usuario.getIdUsuario(),
                juego.getIdJuego(),
                Instant.now(),
                MetodoPagoEnum.PAYPAL,
                50.0,50.0,
                EstadoCompraEnum.COMPLETADA));

        FacturaDto factura = compraControlador.generarFactura(compra.getIdCompra());

        assertNotNull(factura);
        assertEquals(compra.getIdCompra(), factura.idCompra());
    }
}
