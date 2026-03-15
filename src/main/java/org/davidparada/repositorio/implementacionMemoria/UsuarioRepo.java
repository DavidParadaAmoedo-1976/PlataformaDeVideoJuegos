package org.davidparada.repositorio.implementacionMemoria;

import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.formulario.UsuarioForm;
import org.davidparada.modelo.mapper.UsuarioFormularioAEntidadMapper;
import org.davidparada.repositorio.interfaces.IUsuarioRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepo implements IUsuarioRepo {

    private final List<UsuarioEntidad> usuariosEntidad = new ArrayList<>();

    private Long generarId() {
        return usuariosEntidad.stream()
                .mapToLong(u -> u.getIdUsuario())
                .max()
                .orElse(0L) + 1;
    }

    @Override
    public UsuarioEntidad crear(UsuarioForm form) {
        UsuarioEntidad usuario = UsuarioFormularioAEntidadMapper.crearUsuarioEntidad(generarId(), form);

        usuariosEntidad.add(usuario);
        return usuario;
    }

    @Override
    public Optional<UsuarioEntidad> buscarPorId(Long idEntidad) {
        return usuariosEntidad.stream()
                .filter(u -> u.getIdUsuario().equals(idEntidad))
                .findFirst();
    }

    @Override
    public List<UsuarioEntidad> listarTodos() {
        return new ArrayList<>(usuariosEntidad);
    }

    @Override
    public UsuarioEntidad actualizar(Long idEntidad, UsuarioForm form) {
        Optional<UsuarioEntidad> usuario = buscarPorId(idEntidad);
        if (usuario == null) return null;

        UsuarioEntidad nuevoUsuario = UsuarioFormularioAEntidadMapper.actualizarUsuarioEntidad(idEntidad, form);
        usuariosEntidad.remove(usuario);
        usuariosEntidad.add(nuevoUsuario);

        return nuevoUsuario;
    }

    @Override
    public boolean eliminar(Long idEntidad) {
        Optional<UsuarioEntidad> usuarioEntidad = buscarPorId(idEntidad);
        if (usuarioEntidad == null) {
            return false;
        }
        return usuariosEntidad.removeIf(u -> u.getIdUsuario().equals(idEntidad));
    }

    @Override
    public Optional<UsuarioEntidad> buscarPorEmail(String email) {
        return usuariosEntidad.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public Optional<UsuarioEntidad> buscarPorNombreUsuario(String nombreUsuario) {
        return usuariosEntidad.stream()
                .filter(u -> u.getNombreUsuario().equalsIgnoreCase(nombreUsuario))
                .findFirst();
    }
}
