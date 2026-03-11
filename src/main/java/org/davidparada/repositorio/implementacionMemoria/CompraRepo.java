package org.davidparada.repositorio.implementacionMemoria;

import org.davidparada.modelo.entidad.CompraEntidad;
import org.davidparada.modelo.formulario.CompraForm;
import org.davidparada.modelo.mapper.CompraFormularioAEntidadMapper;
import org.davidparada.repositorio.interfaces.ICompraRepo;

import java.util.ArrayList;
import java.util.List;

public class CompraRepo implements ICompraRepo {

    private final List<CompraEntidad> comprasEntidad = new ArrayList<>();


    private Long generarId() {
        return comprasEntidad.stream()
                .mapToLong(compra -> compra.getIdCompra())
                .max()
                .orElse(0L) + 1;
    }

    @Override
    public CompraEntidad crear(CompraForm form) {
        CompraEntidad compraEntidad = CompraFormularioAEntidadMapper.crearCompraEntidad(generarId(), form);
        comprasEntidad.add(compraEntidad);
        return compraEntidad;
    }

    @Override
    public CompraEntidad buscarPorId(Long idEntidad) {
        return comprasEntidad.stream()
                .filter(c -> c.getIdCompra().equals(idEntidad))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<CompraEntidad> listarTodos() {
        return new ArrayList<>(comprasEntidad);
    }

    @Override
    public CompraEntidad actualizar(Long idEntidad, CompraForm form) {
        CompraEntidad compraEntidad = buscarPorId(idEntidad);
        if (compraEntidad == null) {
            return null;
        }
        CompraEntidad nuevaCompra = CompraFormularioAEntidadMapper.actualizarCompraEntidad(idEntidad, form);
        comprasEntidad.remove(compraEntidad);
        comprasEntidad.add(nuevaCompra);

        return nuevaCompra;
    }

    @Override
    public boolean eliminar(Long idEntidad) {
        CompraEntidad compraEntidad = buscarPorId(idEntidad);
        if (compraEntidad == null) return false;
        return comprasEntidad.removeIf(c -> c.getIdCompra().equals(idEntidad));
    }

    @Override
    public List<CompraEntidad> buscarPorUsuario(Long idUsuario) {
        return comprasEntidad.stream()
                .filter(c -> c.getIdUsuario().equals(idUsuario))
                .toList();
    }

    @Override
    public CompraEntidad buscarPorCompraYUsuario(Long idCompra, Long idUsuario) {
        if (idCompra == null || idUsuario == null) return null;
        return comprasEntidad.stream()
                .filter(c -> c.getIdCompra().equals(idCompra) && c.getIdUsuario().equals(idUsuario))
                .findFirst()
                .orElse(null);

    }
}
