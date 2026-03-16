package org.davidparada.repositorio.implementacionMemoria;

import org.davidparada.modelo.entidad.ResenaEntidad;
import org.davidparada.modelo.formulario.ResenaForm;
import org.davidparada.modelo.mapper.ResenaFormularioAEntidadMapper;
import org.davidparada.repositorio.interfaces.IResenaRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResenaRepo implements IResenaRepo {

    private final List<ResenaEntidad> reseniasEntidad = new ArrayList<>();
    private Long siguienteId = 1L;

    private Long generarId() {
        return siguienteId++;
    }

    @Override
    public ResenaEntidad crear(ResenaForm form) {
        ResenaEntidad resenaEntidad = ResenaFormularioAEntidadMapper.crearReseniaEntidad(generarId(), form);
        reseniasEntidad.add(resenaEntidad);
        return resenaEntidad;
    }

    @Override
    public Optional<ResenaEntidad> buscarPorId(Long idEntidad) {
        return reseniasEntidad.stream()
                .filter(r -> r.getIdResena().equals(idEntidad))
                .findFirst();
    }

    @Override
    public List<ResenaEntidad> listarTodos() {
        return new ArrayList<>(reseniasEntidad);
    }

    @Override
    public Optional<ResenaEntidad> actualizar(Long idEntidad, ResenaForm form) {
        ResenaEntidad resenaEntidad = buscarPorId(idEntidad).orElse(null);
        if (resenaEntidad == null) {
            return Optional.empty();
        }
        ResenaEntidad nuevaResenia = ResenaFormularioAEntidadMapper.actualizarReseniaEntidad(idEntidad, form);
        reseniasEntidad.remove(resenaEntidad);
        reseniasEntidad.add(nuevaResenia);
        return Optional.of(nuevaResenia);
    }

    @Override
    public boolean eliminar(Long idEntidad) {
        Optional<ResenaEntidad> resenaEntidad = buscarPorId(idEntidad);
        return resenaEntidad.map(reseniasEntidad::remove).orElse(false);
    }

    @Override
    public List<ResenaEntidad> buscarPorUsuario(Long idUsuario) {
        return reseniasEntidad.stream()
                .filter(r -> r.getIdUsuario().equals(idUsuario))
                .toList();
    }

    @Override
    public List<ResenaEntidad> buscarPorJuego(Long idJuego) {
        return reseniasEntidad.stream()
                .filter(r -> r.getIdJuego().equals(idJuego))
                .toList();
    }

    @Override
    public Optional<ResenaEntidad> buscarPorIdYUsuario(Long idResena, Long idUsuario) {
        return reseniasEntidad.stream()
                .filter(r -> r.getIdResena().equals(idResena) && r.getIdUsuario().equals(idUsuario))
                .findFirst();
    }
}

