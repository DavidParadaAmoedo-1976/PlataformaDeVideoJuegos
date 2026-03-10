package org.DavidParada.repositorio.interfaces;

import org.DavidParada.modelo.entidad.CompraEntidad;
import org.DavidParada.modelo.enums.EstadoCuentaEnum;
import org.DavidParada.modelo.formulario.CompraForm;

import java.util.List;

public interface ICompraRepo extends ICrud<CompraEntidad, CompraForm, Long> {
    List<CompraEntidad> buscarPorUsuario(Long idUsuario);

    CompraEntidad buscarPorCompraYUsuario(Long idCompra, Long idUsuario);
}
