package org.davidparada.repositorio.interfaceRepositorio;

import org.davidparada.modelo.entidad.CompraEntidad;
import org.davidparada.modelo.formulario.CompraForm;

import java.util.List;
import java.util.Optional;

public interface ICompraRepo extends ICrud<CompraEntidad, CompraForm, Long> {
    /**
     * Mostrar las compras del Usuario indicado en el ID.
     *
     * @param idUsuario
     * @return Lista de objetos de la Entidad compra.
     */
    List<CompraEntidad> buscarPorUsuario(Long idUsuario);

    /**
     * Mostrar la compra que tenga el Id indicado y perdtenezca al usuario con el ID indicado
     *
     * @param idCompra
     * @param idUsuario
     * @return
     */
    Optional<CompraEntidad> buscarPorCompraYUsuario(Long idCompra, Long idUsuario);
}
