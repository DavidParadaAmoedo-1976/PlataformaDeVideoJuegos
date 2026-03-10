package org.DavidParada.repositorio.interfaces;

import org.DavidParada.modelo.entidad.ResenaEntidad;
import org.DavidParada.modelo.formulario.ResenaForm;

import java.util.List;

public interface IResenaRepo extends ICrud<ResenaEntidad, ResenaForm, Long> {
    List<ResenaEntidad> buscarPorUsuario(Long idUsuario);

    List<ResenaEntidad> buscarPorJuego(Long idJuego);
}
