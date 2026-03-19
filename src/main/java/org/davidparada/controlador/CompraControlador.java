package org.davidparada.controlador;

import org.davidparada.controlador.interfaceControlador.ICompraControlador;
import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.*;
import org.davidparada.modelo.entidad.BibliotecaEntidad;
import org.davidparada.modelo.entidad.CompraEntidad;
import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.enums.*;
import org.davidparada.modelo.formulario.CompraForm;
import org.davidparada.modelo.formulario.UsuarioForm;
import org.davidparada.modelo.formulario.validacion.ErrorModel;
import org.davidparada.modelo.mapper.CompraEntidadADtoMapper;
import org.davidparada.modelo.mapper.JuegoEntidadADtoMapper;
import org.davidparada.modelo.mapper.UsuarioEntidadADtoMapper;
import org.davidparada.repositorio.interfaceRepositorio.IBibliotecaRepo;
import org.davidparada.repositorio.interfaceRepositorio.ICompraRepo;
import org.davidparada.repositorio.interfaceRepositorio.IJuegoRepo;
import org.davidparada.repositorio.interfaceRepositorio.IUsuarioRepo;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.davidparada.controlador.util.ComprobarErrores.comprobarListaErrores;
import static org.davidparada.controlador.util.ObtenerEntidadesOptional.*;

public class CompraControlador implements ICompraControlador {

    public static final int UNO = 1;
    private static final int FECHA_LIMITE_PARA_REEMBOLSO = 30;
    private static final int HORAS_MAXIMAS_PARA_REEMBOLSO = 5;
    public static final int CERO = 0;
    public static final int DESCUENTO_MIN = 0;
    public static final int DESCUENTO_MAX = 100;
    public static final double POR_CIENTO_DOUBLE = 100.0;
    public static final double PRECIO_MIN = 0d;
    public static final double PRECIO_MAX = 999.9;
    private final ICompraRepo compraRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IJuegoRepo juegoRepo;
    private final IBibliotecaRepo bibliotecaRepo;
    private final BibliotecaControlador bibliotecaControlador;

    public CompraControlador(ICompraRepo compraRepo,
                             IUsuarioRepo usuarioRepo,
                             IJuegoRepo juegoRepo,
                             IBibliotecaRepo bibliotecaRepo,
                             BibliotecaControlador bibliotecaControlador) {

        this.compraRepo = compraRepo;
        this.usuarioRepo = usuarioRepo;
        this.juegoRepo = juegoRepo;
        this.bibliotecaRepo = bibliotecaRepo;
        this.bibliotecaControlador = bibliotecaControlador;
    }

    // Realizar compra
    @Override
    public CompraDto realizarCompra(
            Long idUsuario,
            Long idJuego,
            MetodoPagoEnum metodoPago
    ) throws ValidationException {

        List<ErrorModel> errores = new ArrayList<>();

        if (idUsuario == null) {
            errores.add(new ErrorModel("idUsuario", TipoErrorEnum.OBLIGATORIO));
        }
        if (idJuego == null) {
            errores.add(new ErrorModel("idJuego", TipoErrorEnum.OBLIGATORIO));
        }
        if (metodoPago == null) {
            errores.add(new ErrorModel("metodoPago", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);

        UsuarioEntidad usuario = obtenerUsuario(idUsuario, errores);
        JuegoEntidad juego = obtenerJuego(idJuego, errores);

        if (juego.getEstado() == EstadoJuegoEnum.NO_DISPONIBLE) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.NO_DISPONIBLE));
        }
        comprobarListaErrores(errores);

// Comprueba el estado de la cuenta
        if (usuario.getEstadoCuenta() == EstadoCuentaEnum.SUSPENDIDA) {
            errores.add(new ErrorModel("usuario", TipoErrorEnum.NO_PERMITIDO));
        }
        comprobarListaErrores(errores);

// Compruebo precioBase
        if (juego.getPrecioBase() < PRECIO_MIN || juego.getPrecioBase() > PRECIO_MAX) {
            errores.add(new ErrorModel("descuento", TipoErrorEnum.RANGO_INVALIDO));
        }
        comprobarListaErrores(errores);

// Compruebo descuento válido
        if (juego.getDescuento() < DESCUENTO_MIN || juego.getDescuento() > DESCUENTO_MAX) {
            errores.add(new ErrorModel("descuento", TipoErrorEnum.RANGO_INVALIDO));
        }
        comprobarListaErrores(errores);

// Comprueba si ya está en biblioteca
        boolean enBiblioteca =
                bibliotecaRepo.buscarPorUsuario(idUsuario)
                        .stream()
                        .anyMatch(b -> b.getIdJuego().equals(idJuego));

        if (enBiblioteca) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.DUPLICADO));
        }

// comprueba compras previas
        boolean compraActiva =
                compraRepo.buscarPorUsuario(idUsuario)
                        .stream()
                        .anyMatch(c ->
                                c.getIdJuego().equals(idJuego)
                                        && c.getEstadoCompra() != EstadoCompraEnum.REEMBOLSADA
                        );

        if (compraActiva) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.DUPLICADO));
        }
        comprobarListaErrores(errores);

        CompraForm form = new CompraForm(
                idUsuario,
                idJuego,
                Instant.now(),
                metodoPago,
                juego.getPrecioBase(),
                juego.getDescuento(),
                EstadoCompraEnum.PENDIENTE
        );

        CompraEntidad compra = compraRepo.crear(form);

        return CompraEntidadADtoMapper.compraEntidadADto(compra, usuario, juego);
    }

    // Procesar pago
    @Override
    public void procesarPago(Long idCompra, MetodoPagoEnum metodoPago) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (idCompra == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);
// Compruebo que exista el juego y esté apto para comprar.
        CompraEntidad compraEntidad = obtenerCompra(idCompra, errores);
        if (compraEntidad.getEstadoCompra() == EstadoCompraEnum.COMPLETADA) {
            errores.add(new ErrorModel("compra", TipoErrorEnum.ESTADO_INCORRECTO));
        }
        comprobarListaErrores(errores);

        JuegoEntidad juegoEntidad = obtenerJuego(compraEntidad.getIdJuego(), errores);
        if (estadoJuegoValido(juegoEntidad.getEstado())) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.NO_PERMITIDO));
        }
        comprobarListaErrores(errores);

        // Selecciono metodo de pago
        if (metodoPago != null) {
            switch (metodoPago) {
                case TARJETA -> pagoConTarjeta(idCompra);
                case PAYPAL -> pagoConPaypal(idCompra);
                case TRANSFERENCIA -> pagoConTransferencia(idCompra);
                case CARTERA_STEAM -> pagoConCarteraSteam(idCompra);
                case SALIR -> salir(idCompra);
                default -> throw new IllegalArgumentException("Método de pago no válido");
            }
        }
    }

    private void salir(Long idCompra) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        CompraEntidad compraEntidad = obtenerCompra(idCompra, errores);
        CompraForm nuevaCompra = new CompraForm(
                compraEntidad.getIdUsuario(),
                compraEntidad.getIdJuego(),
                Instant.now(),
                compraEntidad.getMetodoPago(),
                compraEntidad.getPrecioBase(),
                compraEntidad.getDescuento(),
                EstadoCompraEnum.CANCELADA
        );

        compraRepo.actualizar(idCompra, nuevaCompra);
    }

    private void pagoConCarteraSteam(Long idCompra) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        CompraEntidad compraEntidad = obtenerCompra(idCompra, errores);
        UsuarioEntidad usuarioEntidad = obtenerUsuario(compraEntidad.getIdUsuario(), errores);
        JuegoEntidad juegoEntidad = obtenerJuego(compraEntidad.getIdJuego(), errores);

        if (usuarioEntidad.getSaldo() < precioFinal(juegoEntidad.getPrecioBase(),
                juegoEntidad.getDescuento())) {
            errores.add(new ErrorModel("saldo", TipoErrorEnum.SALDO_INSUFICIENTE));
        }

        if (compraEntidad.getEstadoCompra() != EstadoCompraEnum.PENDIENTE) {
            errores.add(new ErrorModel("estado", TipoErrorEnum.NO_PERMITIDO)
            );
        }

        // Modificamos saldo de Usuario
        Double precioJuego = precioFinal(compraEntidad.getPrecioBase(), juegoEntidad.getDescuento());
        if (compraEntidad.getMetodoPago() == MetodoPagoEnum.CARTERA_STEAM) {
            Double nuevoSaldo = usuarioEntidad.getSaldo() - precioJuego;
            usuarioRepo.actualizar(usuarioEntidad.getIdUsuario(), new UsuarioForm(
                    usuarioEntidad.getNombreUsuario(),
                    usuarioEntidad.getEmail(),
                    usuarioEntidad.getPassword(),
                    usuarioEntidad.getNombreReal(),
                    usuarioEntidad.getPais(),
                    usuarioEntidad.getFechaNacimiento(),
                    usuarioEntidad.getFechaRegistro(),
                    usuarioEntidad.getAvatar(),
                    nuevoSaldo,
                    usuarioEntidad.getEstadoCuenta()));
        }

        // Modificamos estado de la compra.
        estadoCompraCompletada(compraEntidad);

        // Añadimos juego a biblioteca
        bibliotecaControlador.anadirJuego(compraEntidad.getIdUsuario(), compraEntidad.getIdJuego());

        comprobarListaErrores(errores);
    }

    private void pagoConTransferencia(Long idCompra) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        CompraEntidad compraEntidad = obtenerCompra(idCompra, errores);

        // Modificamos estado de la compra.
        estadoCompraCompletada(compraEntidad);

        // Añadimos juego a biblioteca
        bibliotecaControlador.anadirJuego(compraEntidad.getIdUsuario(), compraEntidad.getIdJuego());

        comprobarListaErrores(errores);
    }

    private void pagoConPaypal(Long idCompra) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        CompraEntidad compraEntidad = obtenerCompra(idCompra, errores);

        // Modificamos estado de la compra.
        estadoCompraCompletada(compraEntidad);

        // Añadimos juego a biblioteca
        bibliotecaControlador.anadirJuego(compraEntidad.getIdUsuario(), compraEntidad.getIdJuego());

        comprobarListaErrores(errores);
    }

    private void pagoConTarjeta(Long idCompra) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        CompraEntidad compraEntidad = obtenerCompra(idCompra, errores);

        // Modificamos estado de la compra.
        estadoCompraCompletada(compraEntidad);

        // Añadimos juego a biblioteca
        bibliotecaControlador.anadirJuego(compraEntidad.getIdUsuario(), compraEntidad.getIdJuego());

        comprobarListaErrores(errores);
    }

    // Consultar historial de compras
    @Override
    public List<CompraDto> listarCompras(Long idUsuario) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (idUsuario == null) {
            errores.add(new ErrorModel("idUsuario", TipoErrorEnum.OBLIGATORIO));
        }
        UsuarioEntidad usuarioEntidad = obtenerUsuario(idUsuario, errores);

        List<CompraEntidad> comprasEntidad = compraRepo.buscarPorUsuario(idUsuario);
        return comprasEntidad.stream()
                .map(c -> {
                    JuegoEntidad juego = juegoRepo.buscarPorId(c.getIdJuego()).orElse(null);

                    return new CompraDto(
                            c.getIdCompra(),
                            c.getIdUsuario(),
                            UsuarioEntidadADtoMapper.usuarioEntidadADto(usuarioEntidad),
                            c.getIdJuego(),
                            JuegoEntidadADtoMapper.juegoEntidadADto(juego),
                            c.getFechaCompra(),
                            c.getMetodoPago(),
                            c.getPrecioBase(),
                            c.getDescuento(),
                            c.getEstadoCompra()
                    );
                })
                .toList();
    }

    // Consultar compra

    @Override
    public CompraDto consultarCompra(Long idCompra, Long idUsuario) throws ValidationException {

        List<ErrorModel> errores = new ArrayList<>();

        if (idCompra == null) {
            errores.add(new ErrorModel("idCompra", TipoErrorEnum.OBLIGATORIO));
        }

        if (idUsuario == null) {
            errores.add(new ErrorModel("idUsuario", TipoErrorEnum.OBLIGATORIO));
        }

        comprobarListaErrores(errores);

        CompraEntidad compra = obtenerCompra(idCompra, errores);
        UsuarioEntidad usuario = obtenerUsuario(idUsuario, errores);
        JuegoEntidad juego = obtenerJuego(compra.getIdJuego(), errores);

        if (!compra.getIdUsuario().equals(idUsuario)) {
            errores.add(new ErrorModel("usuario", TipoErrorEnum.NO_PERMITIDO));
        }

        comprobarListaErrores(errores);

        return CompraEntidadADtoMapper.compraEntidadADto(compra, usuario, juego);
    }

    // Consultar detalles de una compra
    @Override
    public DetallesCompraDto detallesDeUnaCompra(Long idCompra, Long idUsuario) throws ValidationException {

        List<ErrorModel> errores = new ArrayList<>();

        if (idCompra == null) {
            errores.add(new ErrorModel("idCompra", TipoErrorEnum.OBLIGATORIO));
        }

        if (idUsuario == null) {
            errores.add(new ErrorModel("idUsuario", TipoErrorEnum.OBLIGATORIO));
        }

        comprobarListaErrores(errores);

        UsuarioEntidad usuarioEntidad = obtenerUsuario(idUsuario, errores);
        // Compruebo que exista esa compra asociada a ese usuario
        CompraEntidad compraEntidad = obtenerCompraUsuario(idCompra, idUsuario, errores);
        // Busco el juego asociado a esa compra
        JuegoEntidad juegoEntidad = obtenerJuego(compraEntidad.getIdJuego(), errores);

        CompraDto compraDto = CompraEntidadADtoMapper.compraEntidadADto(
                compraEntidad,
                usuarioEntidad,
                juegoEntidad
        );

        JuegoDto juegoDto = JuegoEntidadADtoMapper.juegoEntidadADto(juegoEntidad);

        FacturaDto facturaDto = generarFactura(idCompra);

        return new DetallesCompraDto(compraDto, juegoDto, facturaDto);
    }

    // Solicitar reembolso
    @Override
    public void solicitarReembolso(Long idCompra) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (idCompra == null) {
            errores.add(new ErrorModel("idCompra", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
        CompraEntidad compraEntidad = obtenerCompra(idCompra, errores);
        if (compraEntidad.getEstadoCompra() != EstadoCompraEnum.COMPLETADA) {
            errores.add(new ErrorModel("estado", TipoErrorEnum.NO_PERMITIDO));
        }
        Instant ahora = Instant.now();
        Instant fechaCompra = compraEntidad.getFechaCompra();
        Duration duracion = Duration.between(fechaCompra, ahora);

        if (duracion.toDays() > FECHA_LIMITE_PARA_REEMBOLSO) {
            errores.add(new ErrorModel("fechaDeCompra", TipoErrorEnum.NO_PERMITIDO));
        }
        comprobarListaErrores(errores);
        BibliotecaEntidad bibliotecaEntidad = obtenerBiblioteca(compraEntidad.getIdUsuario(), compraEntidad.getIdJuego(), errores);

        if (bibliotecaEntidad.getHorasDeJuego() >= HORAS_MAXIMAS_PARA_REEMBOLSO) {
            errores.add(new ErrorModel("horasDeJuego", TipoErrorEnum.NO_PERMITIDO));
        }
        comprobarListaErrores(errores);

        // Busco usuario y juego asociado a la compra
        UsuarioEntidad usuarioEntidad = obtenerUsuario(compraEntidad.getIdUsuario(), errores);

        // Devolver dinero a cartera
        Double precioJuego = precioFinal(compraEntidad.getPrecioBase(), compraEntidad.getDescuento());
        Double nuevoSaldo = usuarioEntidad.getSaldo() + precioJuego;
        usuarioRepo.actualizar(usuarioEntidad.getIdUsuario(), new UsuarioForm(
                usuarioEntidad.getNombreUsuario(),
                usuarioEntidad.getEmail(),
                usuarioEntidad.getPassword(),
                usuarioEntidad.getNombreReal(),
                usuarioEntidad.getPais(),
                usuarioEntidad.getFechaNacimiento(),
                usuarioEntidad.getFechaRegistro(),
                usuarioEntidad.getAvatar(),
                nuevoSaldo,
                usuarioEntidad.getEstadoCuenta()));

        // Cambiar estado
        CompraForm nuevaCompra = new CompraForm(
                compraEntidad.getIdUsuario(),
                compraEntidad.getIdJuego(),
                compraEntidad.getFechaCompra(),
                compraEntidad.getMetodoPago(),
                compraEntidad.getPrecioBase(),
                compraEntidad.getDescuento(),
                EstadoCompraEnum.REEMBOLSADA
        );
        compraRepo.actualizar(idCompra, nuevaCompra);

        // Quitar juego de la biblioteca
        bibliotecaControlador.eliminarJuego(compraEntidad.getIdUsuario(), compraEntidad.getIdJuego());
    }

    // Generar factura
    @Override
    public FacturaDto generarFactura(Long idCompra) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (idCompra == null) {
            errores.add(new ErrorModel("idCompra", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);

        CompraEntidad compraEntidad = obtenerCompra(idCompra, errores);
        UsuarioEntidad usuarioEntidad = obtenerUsuario(compraEntidad.getIdUsuario(), errores);
        obtenerJuego(compraEntidad.getIdJuego(), errores);

        String numeroFactura = generarNumeroFactura(idCompra);
        return new FacturaDto(numeroFactura,
                idCompra,
                usuarioEntidad.getNombreReal(),
                usuarioEntidad.getEmail(),
                compraEntidad.getFechaCompra(),
                precioFinal(compraEntidad.getPrecioBase(), compraEntidad.getDescuento()),
                compraEntidad.getDescuento(),
                compraEntidad.getMetodoPago());
    }

    private void estadoCompraCompletada(CompraEntidad compraEntidad) {
        CompraForm nuevaCompra = new CompraForm(
                compraEntidad.getIdUsuario(),
                compraEntidad.getIdJuego(),
                Instant.now(),
                compraEntidad.getMetodoPago(),
                compraEntidad.getPrecioBase(),
                compraEntidad.getDescuento(),
                EstadoCompraEnum.COMPLETADA
        );
        compraRepo.actualizar(compraEntidad.getIdCompra(), nuevaCompra);
    }

    private boolean estadoJuegoValido(EstadoJuegoEnum estado) {
        return estado != EstadoJuegoEnum.DISPONIBLE
                && estado != EstadoJuegoEnum.PREVENTA
                && estado != EstadoJuegoEnum.ACCESO_ANTICIPADO;
    }

    private Double precioFinal(Double precioBase, Integer descuento) {
        if (descuento == CERO) {
            return precioBase;
        } else {
            return precioBase * (UNO - descuento / POR_CIENTO_DOUBLE);
        }
    }

    private String generarNumeroFactura(Long idCompra) {

        int anio = Instant.now()
                .atZone(java.time.ZoneId.systemDefault())
                .getYear();

        return anio + "-" + String.format("%06d", idCompra);
    }
}