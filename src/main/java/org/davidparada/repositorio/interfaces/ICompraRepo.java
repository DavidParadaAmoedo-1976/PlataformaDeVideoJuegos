package org.davidparada.repositorio.interfaces;

import org.davidparada.modelo.entidad.CompraEntidad;
import org.davidparada.modelo.formulario.CompraForm;

import java.util.List;

public interface ICompraRepo extends ICrud<CompraEntidad, CompraForm, Long> {
    List<CompraEntidad> buscarPorUsuario(Long idUsuario);

    CompraEntidad buscarPorCompraYUsuario(Long idCompra, Long idUsuario);
}
