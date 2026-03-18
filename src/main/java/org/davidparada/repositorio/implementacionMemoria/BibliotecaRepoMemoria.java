package org.davidparada.repositorio.implementacionMemoria;

import org.davidparada.modelo.entidad.BibliotecaEntidad;
import org.davidparada.modelo.formulario.BibliotecaForm;
import org.davidparada.modelo.mapper.BibliotecaFormularioAEntidadMapper;
import org.davidparada.repositorio.interfaceRepositorio.IBibliotecaRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BibliotecaRepoMemoria implements IBibliotecaRepo {
    public static final long INICIALIZAR_CONTADOR_ID = 1L;
    private final List<BibliotecaEntidad> bibliotecasEntidad = new ArrayList<>();
    private Long siguienteId = INICIALIZAR_CONTADOR_ID;

    private Long generarId() {
        return siguienteId++;
    }

    @Override
    public BibliotecaEntidad crear(BibliotecaForm form) {
        BibliotecaEntidad bibliotecaEntidad = BibliotecaFormularioAEntidadMapper.crearBibliotecaEntidad(generarId(), form);
        bibliotecasEntidad.add(bibliotecaEntidad);
        return bibliotecaEntidad;
    }

    @Override
    public Optional<BibliotecaEntidad> buscarPorId(Long idEntidad) {
        return bibliotecasEntidad.stream()
                .filter(b -> b.getIdBiblioteca().equals(idEntidad))
                .findFirst();
    }

    @Override
    public List<BibliotecaEntidad> listarTodos() {
        return new ArrayList<>(bibliotecasEntidad);
    }

    @Override
    public Optional<BibliotecaEntidad> actualizar(Long idEntidad, BibliotecaForm form) {

        BibliotecaEntidad bibliotecaEntidad = buscarPorId(idEntidad).orElse(null);

        if (bibliotecaEntidad == null) {
            return Optional.empty();
        }

        BibliotecaEntidad nuevaBiblioteca =
                BibliotecaFormularioAEntidadMapper.actualizarBibliotecaEntidad(idEntidad, form);

        bibliotecasEntidad.remove(bibliotecaEntidad);
        bibliotecasEntidad.add(nuevaBiblioteca);

        return Optional.of(nuevaBiblioteca);
    }

    @Override
    public boolean eliminar(Long idEntidad) {
        Optional<BibliotecaEntidad> bibliotecaEntidad = buscarPorId(idEntidad);
        if (bibliotecaEntidad.isEmpty()) return false;
        return bibliotecasEntidad.remove(bibliotecaEntidad.get());
    }

    @Override
    public List<BibliotecaEntidad> buscarPorUsuario(Long idUsuario) {
        return bibliotecasEntidad.stream()
                .filter(b -> b.getIdUsuario().equals(idUsuario))
                .toList();
    }

    @Override
    public Optional<BibliotecaEntidad> buscarPorUsuarioYJuego(Long idUsuario, Long idJuego) {
        if (idUsuario == null || idJuego == null) {
            return Optional.empty();
        }

        return bibliotecasEntidad.stream()
                .filter(u -> u.getIdUsuario().equals(idUsuario))
                .filter(j -> j.getIdJuego().equals(idJuego))
                .findFirst();
    }
}
