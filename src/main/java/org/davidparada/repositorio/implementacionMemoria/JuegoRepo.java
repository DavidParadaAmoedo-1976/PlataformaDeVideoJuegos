package org.davidparada.repositorio.implementacionMemoria;

import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.enums.ClasificacionJuegoEnum;
import org.davidparada.modelo.enums.EstadoJuegoEnum;
import org.davidparada.modelo.formulario.JuegoForm;
import org.davidparada.modelo.mapper.JuegoFormularioAEntidadMapper;
import org.davidparada.repositorio.interfaces.IJuegoRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JuegoRepo implements IJuegoRepo {
    private final List<JuegoEntidad> juegosEntidad = new ArrayList<>();
    private Long siguienteId = 1L;

    private Long generarId() {
        return siguienteId++;
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
    public Optional<JuegoEntidad> buscarPorId(Long id) {
        return juegosEntidad.stream()
                .filter(j -> j.getIdJuego().equals(id))
                .findFirst();
    }

    @Override
    public List<JuegoEntidad> listarTodos() {
        return new ArrayList<>(juegosEntidad);
    }

    @Override
    public Optional<JuegoEntidad> actualizar(Long id, JuegoForm form) {
        JuegoEntidad juegoEntidad = buscarPorId(id).orElse(null);
        if (juegoEntidad == null) {
            return Optional.empty();
        }

        JuegoEntidad nuevoJuego = JuegoFormularioAEntidadMapper.actualizarJuegoEntidad(id, form);
        juegosEntidad.remove(juegoEntidad);
        juegosEntidad.add(nuevoJuego);

        return Optional.of(nuevoJuego);
    }

    @Override
    public boolean eliminar(Long id) {
        Optional<JuegoEntidad> juegoEntidad = buscarPorId(id);
        if (juegoEntidad.isEmpty()) {
            return false;
        }
        return juegosEntidad.remove(juegoEntidad.get());
    }
}

