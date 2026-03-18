package org.davidparada.controlador;

import org.davidparada.controlador.interfaceControlador.IJuegoControlador;
import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.JuegoDto;
import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.enums.ClasificacionJuegoEnum;
import org.davidparada.modelo.enums.EstadoJuegoEnum;
import org.davidparada.modelo.enums.OrdenarJuegosEnum;
import org.davidparada.modelo.enums.TipoErrorEnum;
import org.davidparada.modelo.formulario.JuegoForm;
import org.davidparada.modelo.formulario.validacion.ErrorModel;
import org.davidparada.modelo.formulario.validacion.JuegoFormValidador;
import org.davidparada.modelo.mapper.JuegoEntidadADtoMapper;
import org.davidparada.repositorio.interfaceRepositorio.IJuegoRepo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.davidparada.controlador.util.ComprobarErrores.comprobarListaErrores;
import static org.davidparada.controlador.util.ObtenerEntidadesOptional.*;

public class JuegoControlador implements IJuegoControlador {

    public static final int DESCUENTO_MINIMO = 0;
    public static final int DESCUENTO_MAXIMO = 100;
    private final IJuegoRepo juegoRepo;

    public JuegoControlador(IJuegoRepo juegoRepo) {
        this.juegoRepo = juegoRepo;
    }

    // Anadir Juego
    @Override
    public JuegoDto crearJuego(JuegoForm form) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        JuegoFormValidador.validarJuego(form);

        if (juegoRepo.existeTitulo(form.getTitulo())) {
            errores.add(new ErrorModel("titulo", TipoErrorEnum.DUPLICADO));
        }
        comprobarListaErrores(errores);

        JuegoEntidad juegoEntidad = juegoRepo.crear(form);

        return JuegoEntidadADtoMapper.juegoEntidadADto(juegoEntidad);
    }

    // Buscar juegos
    @Override
    public List<JuegoDto> buscarJuegos(
            String titulo,
            String categoria,
            Double precioMin,
            Double precioMax,
            ClasificacionJuegoEnum clasificacion,
            EstadoJuegoEnum estado
    ) {

        List<JuegoEntidad> juegos = juegoRepo.buscarConFiltros(
                titulo, categoria, precioMin, precioMax, clasificacion, estado
        );

        return juegos.stream()
                .map(j -> JuegoEntidadADtoMapper.juegoEntidadADto(j))
                .toList();
    }

    // Consultar catalogo completo
    @Override
    public List<JuegoDto> consultarCatalogo(OrdenarJuegosEnum orden) {

        List<JuegoEntidad> juegos = juegoRepo.listarTodos();

        if (orden != null) {

            switch (orden) {
                case ALFABETICO -> juegos.sort(Comparator.comparing(j -> j.getTitulo()));
                case PRECIO -> juegos.sort(Comparator.comparing(j -> j.getPrecioBase()));
                case FECHA -> juegos.sort(Comparator.comparing(j -> j.getFechaLanzamiento()));
                default -> throw new IllegalArgumentException("No se encontro el tipo de búsqueda");
            }
        }

        return juegos.stream()
                .map(j -> JuegoEntidadADtoMapper.juegoEntidadADto(j))
                .toList();
    }

    // Consultar detalles de un juego
    @Override
    public JuegoDto consultarDetalles(Long idJuego) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (idJuego == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
        JuegoEntidad juego = obtenerJuego(idJuego, errores);

        comprobarListaErrores(errores);

        return JuegoEntidadADtoMapper.juegoEntidadADto(juego);
    }

    // Aplicar descuento
    @Override
    public void aplicarDescuento(Long id, Integer descuento) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (id == null)
            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));

        comprobarListaErrores(errores);

        if (descuento == null) {
            errores.add(new ErrorModel("descuento", TipoErrorEnum.OBLIGATORIO));
        } else if (descuento < DESCUENTO_MINIMO || descuento > DESCUENTO_MAXIMO) {
            errores.add(new ErrorModel("descuento", TipoErrorEnum.RANGO_INVALIDO));
        }
        comprobarListaErrores(errores);

        JuegoEntidad juego = obtenerJuego(id, errores);

        juegoRepo.actualizar(juego.getIdJuego(), new JuegoForm(juego.getTitulo(), juego.getDescripcion(),
                juego.getDesarrollador(), juego.getFechaLanzamiento(),
                juego.getPrecioBase(), descuento,
                juego.getCategoria(), juego.getClasificacionPorEdad(),
                juego.getIdiomas(), juego.getEstado()));
    }

    // Cambiar estado del juego
    @Override
    public void cambiarEstado(Long id, EstadoJuegoEnum nuevoEstado) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (id == null)
            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));

        if (nuevoEstado == null)
            errores.add(new ErrorModel("estado", TipoErrorEnum.OBLIGATORIO));

        comprobarListaErrores(errores);

        JuegoEntidad juego = obtenerJuego(id, errores);

        juegoRepo.actualizar(juego.getIdJuego(), new JuegoForm(juego.getTitulo(), juego.getDescripcion(),
                juego.getDesarrollador(), juego.getFechaLanzamiento(),
                juego.getPrecioBase(), juego.getDescuento(),
                juego.getCategoria(), juego.getClasificacionPorEdad(),
                juego.getIdiomas(), nuevoEstado));
    }

    // Eliminar el juego

    // Método no aparece en la gestion de juego.
    // Se deja comentado por si hace falta en el futuro.

    @Override
    public boolean eliminar(Long id) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();
        if (id == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));
        }
        obtenerJuego(id, errores);

        return juegoRepo.eliminar(id);
    }
}
