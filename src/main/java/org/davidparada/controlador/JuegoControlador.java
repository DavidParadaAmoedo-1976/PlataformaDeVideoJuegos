package org.DavidParada.controlador;

import org.DavidParada.excepcion.ValidationException;
import org.DavidParada.modelo.dto.JuegoDto;
import org.DavidParada.modelo.entidad.JuegoEntidad;
import org.DavidParada.modelo.enums.ClasificacionJuegoEnum;
import org.DavidParada.modelo.enums.EstadoJuegoEnum;
import org.DavidParada.modelo.enums.OrdenarJuegosEnum;
import org.DavidParada.modelo.enums.TipoErrorEnum;
import org.DavidParada.modelo.formulario.JuegoForm;
import org.DavidParada.modelo.formulario.validacion.ErrorModel;
import org.DavidParada.modelo.formulario.validacion.JuegoFormValidador;
import org.DavidParada.modelo.mapper.JuegoEntidadADtoMapper;
import org.DavidParada.repositorio.interfaces.IJuegoRepo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JuegoControlador {

    private final IJuegoRepo juegoRepo;

    public JuegoControlador(IJuegoRepo juegoRepo) {
        this.juegoRepo = juegoRepo;
    }

    // Anadir Juego

    public JuegoDto crearJuego(JuegoForm form) throws ValidationException {

        JuegoFormValidador.validarJuego(form);

        JuegoEntidad juegoEntidad = juegoRepo.crear(form);

        return JuegoEntidadADtoMapper.juegoEntidadADto(juegoEntidad);
    }

    // Buscar juegos

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

    public List<JuegoDto> consultarCatalogo(OrdenarJuegosEnum orden) {

        List<JuegoEntidad> juegos = juegoRepo.listarTodos();

        if (orden != null) {

            switch (orden) {

                case ALFABETICO -> juegos.sort(Comparator.comparing(j -> j.getTitulo()));

                case PRECIO -> juegos.sort(Comparator.comparing(j -> j.getPrecioBase()));

                case FECHA -> juegos.sort(Comparator.comparing(j -> j.getFechaLanzamiento()));
            }
        }

        return juegos.stream()
                .map(j -> JuegoEntidadADtoMapper.juegoEntidadADto(j))
                .toList();
    }

    // Consultar detalles de un juego

    public JuegoDto consultarDetalles(Long idJuego) throws ValidationException {

        List<ErrorModel> errores = new ArrayList<>();

        if (idJuego == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);
        JuegoEntidad juego = juegoRepo.buscarPorId(idJuego);
        if (juego == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);

        return JuegoEntidadADtoMapper.juegoEntidadADto(juego);
    }

    // Aplicar descuento

    public void aplicarDescuento(Long id, Integer descuento) throws ValidationException {

        List<ErrorModel> errores = new ArrayList<>();

        if (id == null)
            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));

        if (descuento == null) {
            errores.add(new ErrorModel("descuento", TipoErrorEnum.OBLIGATORIO));
        }
        comprobarListaErrores(errores);

        if (descuento < 0 || descuento > 100)
            errores.add(new ErrorModel("descuento", TipoErrorEnum.RANGO_INVALIDO));

        comprobarListaErrores(errores);

        JuegoEntidad juego = juegoRepo.buscarPorId(id);

        if (juego == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);
        juegoRepo.actualizar(juego.getIdJuego(), new JuegoForm(juego.getTitulo(), juego.getDescripcion(),
                juego.getDesarrollador(), juego.getFechaLanzamiento(),
                juego.getPrecioBase(), descuento,
                juego.getCategoria(), juego.getClasificacionPorEdad(),
                juego.getIdiomas(), juego.getEstado()));
    }

    // Cambiar estado del juego

    public void cambiarEstado(Long id, EstadoJuegoEnum nuevoEstado) throws ValidationException {
        List<ErrorModel> errores = new ArrayList<>();

        if (id == null)
            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));

        if (nuevoEstado == null)
            errores.add(new ErrorModel("estado", TipoErrorEnum.OBLIGATORIO));

        comprobarListaErrores(errores);

        JuegoEntidad juego = juegoRepo.buscarPorId(id);

        if (juego == null) {
            errores.add(new ErrorModel("id", TipoErrorEnum.NO_ENCONTRADO));
        }
        comprobarListaErrores(errores);

        juegoRepo.actualizar(juego.getIdJuego(), new JuegoForm(juego.getTitulo(), juego.getDescripcion(),
                juego.getDesarrollador(), juego.getFechaLanzamiento(),
                juego.getPrecioBase(), juego.getDescuento(),
                juego.getCategoria(), juego.getClasificacionPorEdad(),
                juego.getIdiomas(), nuevoEstado));
    }

    private void comprobarListaErrores(List<ErrorModel> errores) throws ValidationException {
        if (!errores.isEmpty()) {
            throw new ValidationException(errores);
        }
    }

    // Eliminar el juego

    // Método no aparece en la gestion de juego.
    // Se deja comentado por si hace falta en el futuro.

//    public boolean eliminar(Long id) {
//        List<ErrorModel> errores = new ArrayList<>();
//        if (id == null){
//            errores.add(new ErrorModel("id", TipoErrorEnum.OBLIGATORIO));
//        } else {
//            JuegoEntidad juego = juegoRepo.buscarPorId(id);
//            if (juego == null) {
//                errores.add(new ErrorModel("id", TipoErrorEnum.NO_ENCONTRADO));
//            }
//        }
//        comprobarListaErrores(errores);
//
//        return juegoRepo.eliminar(id);
//    }
}
