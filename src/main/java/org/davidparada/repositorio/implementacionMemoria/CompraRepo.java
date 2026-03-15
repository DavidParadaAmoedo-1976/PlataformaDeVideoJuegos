package org.davidparada.repositorio.implementacionMemoria;

import org.davidparada.modelo.entidad.CompraEntidad;
import org.davidparada.modelo.formulario.CompraForm;
import org.davidparada.modelo.mapper.CompraFormularioAEntidadMapper;
import org.davidparada.repositorio.interfaces.ICompraRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompraRepo implements ICompraRepo {

    private final List<CompraEntidad> comprasEntidad = new ArrayList<>();
    private Long siguienteId = 1L;

    private Long generarId() {
        return siguienteId++;
    }

    @Override
    public CompraEntidad crear(CompraForm form) {
        CompraEntidad compraEntidad = CompraFormularioAEntidadMapper.crearCompraEntidad(generarId(), form);
        comprasEntidad.add(compraEntidad);
        return compraEntidad;
    }

    @Override
    public Optional<CompraEntidad> buscarPorId(Long idEntidad) {
        return comprasEntidad.stream()
                .filter(c -> c.getIdCompra().equals(idEntidad))
                .findFirst();
    }

    @Override
    public List<CompraEntidad> listarTodos() {
        return new ArrayList<>(comprasEntidad);
    }

    @Override
    public Optional<CompraEntidad> actualizar(Long idEntidad, CompraForm form) {
        CompraEntidad compraEntidad = buscarPorId(idEntidad).orElse(null);
        if (compraEntidad == null) {
            return Optional.empty();
        }
        CompraEntidad nuevaCompra = CompraFormularioAEntidadMapper.actualizarCompraEntidad(idEntidad, form);
        comprasEntidad.remove(compraEntidad);
        comprasEntidad.add(nuevaCompra);

        return Optional.of(nuevaCompra);
    }

    @Override
    public boolean eliminar(Long idEntidad) {
        Optional<CompraEntidad> compraEntidad = buscarPorId(idEntidad);
        if (compraEntidad.isEmpty()) {
            return false;
        }
        return comprasEntidad.remove(compraEntidad.get());
    }

    @Override
    public List<CompraEntidad> buscarPorUsuario(Long idUsuario) {
        return comprasEntidad.stream()
                .filter(c -> c.getIdUsuario().equals(idUsuario))
                .toList();
    }

    @Override
    public Optional<CompraEntidad> buscarPorCompraYUsuario(Long idCompra, Long idUsuario) {
        if (idCompra == null || idUsuario == null) return Optional.empty();
        return comprasEntidad.stream()
                .filter(c -> c.getIdCompra().equals(idCompra) && c.getIdUsuario().equals(idUsuario))
                .findFirst();
    }
}
