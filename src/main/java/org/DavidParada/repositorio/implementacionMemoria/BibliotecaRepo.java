package org.DavidParada.repositorio.implementacionMemoria;

import org.DavidParada.modelo.entidad.BibliotecaEntidad;
import org.DavidParada.modelo.formulario.BibliotecaForm;
import org.DavidParada.modelo.mapper.BibliotecaFormularioAEntidadMapper;
import org.DavidParada.repositorio.interfaces.IBibliotecaRepo;

import java.util.ArrayList;
import java.util.List;

public class BibliotecaRepo implements IBibliotecaRepo {
    private final List<BibliotecaEntidad> bibliotecasEntidad = new ArrayList<>();

    @Override
    public BibliotecaEntidad crear(BibliotecaForm form) {
        BibliotecaEntidad bibliotecaEntidad = BibliotecaFormularioAEntidadMapper.crearBibliotecaEntidad(generarId(), form);
        bibliotecasEntidad.add(bibliotecaEntidad);
        return bibliotecaEntidad;
    }

    @Override
    public BibliotecaEntidad buscarPorId(Long idEntidad) {
        return bibliotecasEntidad.stream()
                .filter(b -> b.getIdBiblioteca().equals(idEntidad))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<BibliotecaEntidad> listarTodos() {
        return new ArrayList<>(bibliotecasEntidad);
    }

    @Override
    public BibliotecaEntidad actualizar(Long id, BibliotecaForm form) {
        BibliotecaEntidad bibliotecaEntidad = buscarPorId(id);
        if (bibliotecaEntidad == null) return null;

        BibliotecaEntidad nuevaBiblioteca = BibliotecaFormularioAEntidadMapper.actualizarBibliotecaEntidad(id, form);
        bibliotecasEntidad.remove(bibliotecaEntidad);
        bibliotecasEntidad.add(nuevaBiblioteca);

        return nuevaBiblioteca;
    }

    @Override
    public boolean eliminar(Long id) {
        BibliotecaEntidad bibliotecaEntidad = buscarPorId(id);
        if (bibliotecaEntidad == null) return false;
        return bibliotecasEntidad.removeIf(b -> b.getIdBiblioteca().equals(id));
    }

    private Long generarId() {
        return bibliotecasEntidad.stream()
                .mapToLong(biblioteca -> biblioteca.getIdBiblioteca())
                .max()
                .orElse(0L) + 1;
    }

    @Override
    public List<BibliotecaEntidad> buscarPorUsuario(Long idUsuario) {
        return bibliotecasEntidad.stream()
                .filter(b -> b.getIdUsuario().equals(idUsuario))
                .toList();
    }

    @Override
    public BibliotecaEntidad buscarPorUsuarioYJuego(Long idUsuario, Long idJuego) {
        if (idUsuario == null || idJuego == null) {
            return null;
        }

        return bibliotecasEntidad.stream()
                .filter(u -> u.getIdUsuario().equals(idUsuario))
                .filter(j -> j.getIdJuego().equals(idJuego))
                .findFirst()
                .orElse(null);
    }
}
