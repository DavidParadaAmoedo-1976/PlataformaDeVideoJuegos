package org.davidparada.controlador;

import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.*;
import org.davidparada.modelo.entidad.BibliotecaEntidad;
import org.davidparada.modelo.entidad.CompraEntidad;
import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.enums.*;
import org.davidparada.modelo.formulario.CompraForm;
import org.davidparada.modelo.formulario.UsuarioForm;
import org.davidparada.modelo.formulario.validacion.CompraFormValidador;
import org.davidparada.modelo.formulario.validacion.ErrorModel;
import org.davidparada.modelo.mapper.CompraEntidadADtoMapper;
import org.davidparada.modelo.mapper.JuegoEntidadADtoMapper;
import org.davidparada.modelo.mapper.UsuarioEntidadADtoMapper;
import org.davidparada.repositorio.interfaces.IBibliotecaRepo;
import org.davidparada.repositorio.interfaces.ICompraRepo;
import org.davidparada.repositorio.interfaces.IJuegoRepo;
import org.davidparada.repositorio.interfaces.IUsuarioRepo;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.davidparada.controlador.util.ComprobarErrores.comprobarListaErrores;

public class CompraControlador {

    private static final int FECHA_LIMITE_PARA_REEMBOLSO = 30;
    private static final int HORAS_MAXIMAS_PARA_REEMBOLSO = 5;
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

    public CompraDto realizarCompra(CompraForm form) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        // Validación básica
        CompraFormValidador.validarCompra(form);

        // Buscamos el usuario
        UsuarioEntidad usuarioEntidad = usuarioRepo.buscarPorId(form.getIdUsuario());
        if (usuarioEntidad == null) {
            errores.add(new ErrorModel("usuario", TipoErrorEnum.NO_ENCONTRADO));
        } else if (!usuarioEntidad.getEstadoCuenta().equals(EstadoCuentaEnum.ACTIVA)) {
            errores.add(new ErrorModel("usuario", TipoErrorEnum.NO_PERMITIDO));
        }

        // Buscamos el juego
        JuegoEntidad juegoEntidad = juegoRepo.buscarPorId(form.getIdJuego());
        if (juegoEntidad == null) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.NO_ENCONTRADO));
        } else if (!estadoJuegoValido(juegoEntidad.getEstado())) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.NO_PERMITIDO));
        }

        // Comprobamos que el juego no este ya en la biblioteca
        if (bibliotecaRepo.buscarPorUsuarioYJuego(form.getIdUsuario(), form.getIdJuego()) != null) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.DUPLICADO));
        }

        comprobarListaErrores(errores);

        // Crear compra
        CompraForm compraForm = new CompraForm(
                form.getIdUsuario(),
                form.getIdJuego(),
                Instant.now(),
                form.getMetodoPago(),
                juegoEntidad.getPrecioBase(),
                precioFinal(juegoEntidad.getPrecioBase(),juegoEntidad.getDescuento()),
                EstadoCompraEnum.PENDIENTE
        );

        CompraEntidad compraEntidad = compraRepo.crear(compraForm);

        // Si paga con cartera
        if (form.getMetodoPago() == MetodoPagoEnum.CARTERA_STEAM) {
            pagoConCarteraSteam(compraEntidad.getIdCompra());
        }


        return CompraEntidadADtoMapper.compraEntidadADto(compraEntidad, usuarioEntidad, juegoEntidad);
    }

    // Procesar pago

    public boolean procesarPago(Long idCompra, MetodoPagoEnum metodoPago) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (idCompra == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.NO_ENCONTRADO));
        }

        // Compruebo que exista el juego y esté apto para comprar.
        CompraEntidad compraEntidad = compraRepo.buscarPorId(idCompra);

        JuegoEntidad juegoEntidad = juegoRepo.buscarPorId(compraEntidad.getIdJuego());
        if (juegoEntidad == null) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.NO_ENCONTRADO));
        } else if (!estadoJuegoValido(juegoEntidad.getEstado())) {
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
            }
        }
        return true;

    }

    private void salir(Long idCompra) {
        CompraEntidad compraEntidad = compraRepo.buscarPorId(idCompra);
        CompraForm nuevaCompra = new CompraForm(
                compraEntidad.getIdUsuario(),
                compraEntidad.getIdJuego(),
                Instant.now(),
                compraEntidad.getMetodoPago(),
                compraEntidad.getPrecioBase(),
                precioFinal(compraEntidad.getPrecioBase(), compraEntidad.getDescuento()),
                EstadoCompraEnum.CANCELADA
        );

        compraRepo.actualizar(idCompra, nuevaCompra);
    }

    private boolean pagoConCarteraSteam(Long idCompra) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        CompraEntidad compraEntidad = compraRepo.buscarPorId(idCompra);
        UsuarioEntidad usuarioEntidad = usuarioRepo.buscarPorId(compraEntidad.getIdUsuario());
        JuegoEntidad juegoEntidad = juegoRepo.buscarPorId(compraEntidad.getIdJuego());

        if (usuarioEntidad.getSaldo() < precioFinal(juegoEntidad.getPrecioBase(), juegoEntidad.getDescuento())) {
            errores.add(new ErrorModel("saldo", TipoErrorEnum.SALDO_INSUFICIENTE));
        }

        if (compraEntidad.getEstadoCompra() != EstadoCompraEnum.PENDIENTE) {
            errores.add(new ErrorModel("estado", TipoErrorEnum.NO_PERMITIDO)
            );
        }

        // Modificamos saldo de Usuario
        Double precioJuego = precioFinal(compraEntidad.getPrecioBase(), juegoEntidad.getDescuento());
        if (compraEntidad.getMetodoPago() == MetodoPagoEnum.CARTERA_STEAM) {

//            if (usuarioEntidad.getSaldo() < precioJuego) {
//                errores.add(new ErrorModel("saldo", TipoErrorEnum.SALDO_INSUFICIENTE)
//                );
//            }
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

        estadoCompraCompletada(compraEntidad, juegoEntidad);

        // Añadimos juego a biblioteca
        bibliotecaControlador.anadirJuego(compraEntidad.getIdUsuario(), compraEntidad.getIdJuego());

        comprobarListaErrores(errores);
        return true;
    }



    private boolean pagoConTransferencia(Long idCompra) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        CompraEntidad compraEntidad = compraRepo.buscarPorId(idCompra);
        JuegoEntidad juegoEntidad = juegoRepo.buscarPorId(compraEntidad.getIdJuego());
        // Modificamos estado de la compra.

        estadoCompraCompletada(compraEntidad, juegoEntidad);

        // Añadimos juego a biblioteca
        bibliotecaControlador.anadirJuego(compraEntidad.getIdUsuario(), compraEntidad.getIdJuego());

        comprobarListaErrores(errores);
        return true;
    }

    private boolean pagoConPaypal(Long idCompra)throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        CompraEntidad compraEntidad = compraRepo.buscarPorId(idCompra);
        JuegoEntidad juegoEntidad = juegoRepo.buscarPorId(compraEntidad.getIdJuego());
        // Modificamos estado de la compra.

        estadoCompraCompletada(compraEntidad, juegoEntidad);

        // Añadimos juego a biblioteca
        bibliotecaControlador.anadirJuego(compraEntidad.getIdUsuario(), compraEntidad.getIdJuego());

        comprobarListaErrores(errores);
        return true;
    }

    private boolean pagoConTarjeta(Long idCompra)throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        CompraEntidad compraEntidad = compraRepo.buscarPorId(idCompra);
        JuegoEntidad juegoEntidad = juegoRepo.buscarPorId(compraEntidad.getIdJuego());
        // Modificamos estado de la compra.

        estadoCompraCompletada(compraEntidad, juegoEntidad);

        // Añadimos juego a biblioteca
        bibliotecaControlador.anadirJuego(compraEntidad.getIdUsuario(), compraEntidad.getIdJuego());

        comprobarListaErrores(errores);
        return true;
    }

    // Consultar historial de compras
    public List<CompraDto> listarCompras(Long idUsuario) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (idUsuario == null) {
            errores.add(new ErrorModel("idUsuario", TipoErrorEnum.OBLIGATORIO));
        }
        UsuarioEntidad usuarioEntidad = usuarioRepo.buscarPorId(idUsuario);
        if (usuarioEntidad == null) {
            errores.add(new ErrorModel("idUsuario", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);

        List<CompraEntidad> comprasEntidad = compraRepo.buscarPorUsuario(idUsuario);

        List<CompraDto> compras = comprasEntidad.stream()
                .map(c -> {
                    JuegoEntidad juegoEntidad = juegoRepo.buscarPorId(c.getIdJuego());
                    return new CompraDto(
                            c.getIdCompra(),
                            c.getIdUsuario(),
                            UsuarioEntidadADtoMapper.usuarioEntidadADto(usuarioEntidad),
                            c.getIdJuego(),
                            JuegoEntidadADtoMapper.juegoEntidadADto(juegoEntidad),
                            c.getFechaCompra(),
                            c.getMetodoPago(),
                            c.getPrecioBase(),
                            c.getDescuento(),
                            c.getEstadoCompra()

                    );
                })
                .toList();

        return compras;
    }
    // Consultar detalles de una compra
    public DetallesCompraDto detallesDeUnaCompra(Long idCompra, Long idUsuario) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        if (idCompra == null) {
            errores.add(new ErrorModel("idCompra", TipoErrorEnum.OBLIGATORIO));
        }
        if (idUsuario == null) {
            errores.add(new ErrorModel("idUsuario", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);

        // Compruebo que exista el usuario y la compra
        UsuarioEntidad usuarioEntidad = usuarioRepo.buscarPorId(idUsuario);
        if (usuarioEntidad == null) {
            errores.add(new ErrorModel("usuario", TipoErrorEnum.NO_ENCONTRADO));
        }

        // Compruebo que exista esa compra asociada a ese usuario
        CompraEntidad compraEntidad = compraRepo.buscarPorCompraYUsuario(idCompra, idUsuario);
        if (compraEntidad == null) {
            errores.add(new ErrorModel("compra", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);

        // Busco el juego asociado a esa compra
        JuegoEntidad juegoEntidad = juegoRepo.buscarPorId(compraEntidad.getIdJuego());
        if (juegoEntidad == null) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);
        CompraDto compraDto = CompraEntidadADtoMapper.compraEntidadADto(compraEntidad, usuarioEntidad, juegoEntidad);
        JuegoDto juegoDto = JuegoEntidadADtoMapper.juegoEntidadADto(juegoEntidad);
        FacturaDto facturaDto = generarFactura(idCompra);
        return new DetallesCompraDto(compraDto,juegoDto,facturaDto);
    }

    // Solicitar reembolso
    public void solicitarReembolso(Long idCompra) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (idCompra == null) {
            errores.add(new ErrorModel("idCompra", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
        CompraEntidad compraEntidad = compraRepo.buscarPorId(idCompra);
        if (compraEntidad == null) {
            errores.add(new ErrorModel("compra", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);
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
        BibliotecaEntidad bibliotecaEntidad = bibliotecaRepo.buscarPorUsuarioYJuego(compraEntidad.getIdUsuario(), compraEntidad.getIdJuego());
        if (bibliotecaEntidad == null) {
            errores.add(new ErrorModel("biblioteca", TipoErrorEnum.NO_ENCONTRADO));
        } else if (bibliotecaEntidad.getHorasDeJuego() >= HORAS_MAXIMAS_PARA_REEMBOLSO){
            errores.add(new ErrorModel("horasDeJuego", TipoErrorEnum.NO_PERMITIDO));
        }
        comprobarListaErrores(errores);
        
        // Busco usuario y juego asociado a la compra
        UsuarioEntidad usuarioEntidad = usuarioRepo.buscarPorId(compraEntidad.getIdUsuario());
        if (usuarioEntidad == null) {
            errores.add(new ErrorModel("usuario", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);
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
                precioFinal(compraEntidad.getPrecioBase(), compraEntidad.getDescuento()),
                EstadoCompraEnum.REEMBOLSADA
        );
        compraRepo.actualizar(idCompra, nuevaCompra);

        // Quitar juego de la biblioteca
        bibliotecaControlador.eliminarJuego(compraEntidad.getIdUsuario(),compraEntidad.getIdJuego());
    }

    // Generar factura
    public FacturaDto generarFactura(Long idCompra) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        if (idCompra == null) {
            errores.add(new ErrorModel("idCompra", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
        CompraEntidad compraEntidad = compraRepo.buscarPorId(idCompra);
        if (compraEntidad == null) {
            errores.add(new ErrorModel("compra", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);
        UsuarioEntidad usuarioEntidad = usuarioRepo.buscarPorId(compraEntidad.getIdUsuario());
        if (usuarioEntidad == null) {
            errores.add(new ErrorModel("usuario", TipoErrorEnum.NO_ENCONTRADO));
        }
        JuegoEntidad juegoEntidad = juegoRepo.buscarPorId(compraEntidad.getIdJuego());
        if (juegoEntidad == null) {
            errores.add(new ErrorModel("juego", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);

        // igualo numero de factura a idCompra, no estoy segura de tener que guardarlas,
        // en ese caso tengo que crear entidad y repositorio.
        Long numeroFactura = idCompra;
        FacturaDto nuevaFacturaDto = new FacturaDto(numeroFactura,
                idCompra,
                usuarioEntidad.getNombreReal(),
                usuarioEntidad.getEmail(),
                compraEntidad.getFechaCompra(),
                precioFinal(compraEntidad.getPrecioBase(),compraEntidad.getDescuento()),
                compraEntidad.getDescuento(),
                compraEntidad.getMetodoPago());
        return nuevaFacturaDto;
    }

    private CompraEntidad estadoCompraCompletada(CompraEntidad compraEntidad, JuegoEntidad juegoEntidad) {
        CompraForm nuevaCompra = new CompraForm(
                compraEntidad.getIdUsuario(),
                compraEntidad.getIdJuego(),
                Instant.now(),
                compraEntidad.getMetodoPago(),
                compraEntidad.getPrecioBase(),
                precioFinal(juegoEntidad.getPrecioBase(),compraEntidad.getDescuento()),
                EstadoCompraEnum.COMPLETADA
        );

        compraEntidad = compraRepo.actualizar(compraEntidad.getIdCompra(), nuevaCompra);
        return compraEntidad;
    }
    
    private boolean estadoJuegoValido(EstadoJuegoEnum estado) {
        return estado == EstadoJuegoEnum.DISPONIBLE
                || estado == EstadoJuegoEnum.PREVENTA
                || estado == EstadoJuegoEnum.ACCESO_ANTICIPADO;
    }

    private Double precioFinal(Double precioBase, Integer descuento) {
        if (descuento == 0) {
            return precioBase;
        } else {
            return precioBase * (1 - descuento / 100.0);
        }
    }
}


