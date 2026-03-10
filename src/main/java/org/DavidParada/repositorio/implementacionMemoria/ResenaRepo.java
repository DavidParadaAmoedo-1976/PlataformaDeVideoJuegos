package org.DavidParada.repositorio.implementacionMemoria;

import org.DavidParada.modelo.entidad.ResenaEntidad;
import org.DavidParada.modelo.formulario.ResenaForm;
import org.DavidParada.modelo.mapper.ResenaFormularioAEntidadMapper;
import org.DavidParada.repositorio.interfaces.IResenaRepo;

import java.util.ArrayList;
import java.util.List;

public class ResenaRepo implements IResenaRepo {

    private final List<ResenaEntidad> reseniasEntidad = new ArrayList<>();


    private Long generarId() {
        return reseniasEntidad.stream()
                .mapToLong(resenia -> resenia.getIdResena())
                .max()
                .orElse(0L) + 1;
    }

    @Override
    public ResenaEntidad crear(ResenaForm form) {
        ResenaEntidad resenaEntidad = ResenaFormularioAEntidadMapper.crearReseniaEntidad(generarId(), form);
        reseniasEntidad.add(resenaEntidad);
        return resenaEntidad;
    }

    @Override
    public ResenaEntidad buscarPorId(Long idEntidad) {
        return reseniasEntidad.stream()
                .filter(r -> r.getIdResena().equals(idEntidad))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ResenaEntidad> listarTodos() {
        return new ArrayList<>(reseniasEntidad);
    }

    @Override
    public ResenaEntidad actualizar(Long idEntidad, ResenaForm form) {
        ResenaEntidad resenaEntidad = buscarPorId(idEntidad);
        if (resenaEntidad == null) {
            return null;
        }
        ResenaEntidad nuevaResenia = ResenaFormularioAEntidadMapper.actualizarReseniaEntidad(idEntidad, form);
        reseniasEntidad.remove(resenaEntidad);
        reseniasEntidad.add(nuevaResenia);
        return nuevaResenia;
    }

    @Override
    public boolean eliminar(Long idEntidad) {
        ResenaEntidad resenaEntidad = buscarPorId(idEntidad);
        if (resenaEntidad == null) {
            return false;
        }
        return reseniasEntidad.removeIf(r -> r.getIdResena().equals(idEntidad));
    }

    @Override
    public List<ResenaEntidad> buscarPorUsuario(Long idUsuario){
        return reseniasEntidad.stream()
                .filter(r -> r.getIdUsuario().equals(idUsuario))
                .toList();
    }

    @Override
    public List<ResenaEntidad> buscarPorJuego(Long idJuego){
        return reseniasEntidad.stream()
                .filter(r -> r.getIdJuego().equals(idJuego))
                .toList();
    }
}

