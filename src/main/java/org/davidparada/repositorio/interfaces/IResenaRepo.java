package org.davidparada.repositorio.interfaces;

import org.davidparada.modelo.entidad.ResenaEntidad;
import org.davidparada.modelo.formulario.ResenaForm;

import java.util.List;
import java.util.Optional;

public interface IResenaRepo extends ICrud<ResenaEntidad, ResenaForm, Long> {
    List<ResenaEntidad> buscarPorUsuario(Long idUsuario);

    List<ResenaEntidad> buscarPorJuego(Long idJuego);

    Optional<ResenaEntidad> buscarPorIdYUsuario(Long idResena, Long idUsuario);
}

