package org.DavidParada.repositorio.implementacionMemoria;

import org.DavidParada.modelo.entidad.JuegoEntidad;
import org.DavidParada.modelo.enums.ClasificacionJuegoEnum;
import org.DavidParada.modelo.enums.EstadoJuegoEnum;
import org.DavidParada.modelo.formulario.JuegoForm;
import org.DavidParada.modelo.mapper.JuegoFormularioAEntidadMapper;
import org.DavidParada.repositorio.interfaces.IJuegoRepo;

import java.util.ArrayList;
import java.util.List;

public class JuegoRepo implements IJuegoRepo {
    private final List<JuegoEntidad> juegosEntidad = new ArrayList<>();


    private Long generarId() {
        return juegosEntidad.stream()
                .mapToLong(j -> j.getIdJuego())
                .max()
                .orElse(0L) + 1;
    }

    public List<JuegoEntidad> buscarConFiltros(
            String titulo,
            String categoria,
            Double precioMin,
            Double precioMax,
            ClasificacionJuegoEnum clasificacion,
            EstadoJuegoEnum estado
    ) {

        return juegosEntidad.stream()

                .filter(j -> titulo == null ||
                        j.getTitulo().toLowerCase().contains(titulo.toLowerCase()))

                .filter(j -> categoria == null ||
                        j.getCategoria().equalsIgnoreCase(categoria))

                .filter(j -> precioMin == null ||
                        j.getPrecioBase() >= precioMin)

                .filter(j -> precioMax == null ||
                        j.getPrecioBase() <= precioMax)

                .filter(j -> clasificacion == null ||
                        j.getClasificacionPorEdad() == clasificacion)

                .filter(j -> estado == null ||
                        j.getEstado() == estado)

                .toList();
    }

    @Override
    public boolean existeTitulo(String titulo) {
        return juegosEntidad.stream()
                .anyMatch(j -> j.getTitulo().equalsIgnoreCase(titulo));
    }


    @Override
    public JuegoEntidad crear(JuegoForm form) {
        JuegoEntidad juegoEntidad = JuegoFormularioAEntidadMapper.crearJuegoEntidad(generarId(), form);
        juegosEntidad.add(juegoEntidad);

        return juegoEntidad;
    }


    @Override
    public JuegoEntidad buscarPorId(Long id) {
        return juegosEntidad.stream()
                .filter(j -> j.getIdJuego().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<JuegoEntidad> listarTodos() {
        return new ArrayList<>(juegosEntidad);
    }

    @Override
    public JuegoEntidad actualizar(Long id, JuegoForm form) {
        JuegoEntidad juegoEntidad = buscarPorId(id);
        if (juegoEntidad == null) return null;

        JuegoEntidad nuevoJuego = JuegoFormularioAEntidadMapper.actualizarJuegoEntidad(id, form);
        juegosEntidad.remove(juegoEntidad);
        juegosEntidad.add(nuevoJuego);

        return nuevoJuego;
    }

    @Override
    public boolean eliminar(Long id) {
        JuegoEntidad juegoEntidad = buscarPorId(id);
        if (juegoEntidad == null) {
            return false;
        }
        return juegosEntidad.removeIf(j -> j.getIdJuego().equals(id));
    }

}

