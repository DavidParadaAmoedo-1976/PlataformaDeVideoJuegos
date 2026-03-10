package org.DavidParada.repositorio.implementacionMemoria;

import org.DavidParada.modelo.entidad.UsuarioEntidad;
import org.DavidParada.modelo.formulario.UsuarioForm;
import org.DavidParada.modelo.mapper.UsuarioFormularioAEntidadMapper;
import org.DavidParada.repositorio.interfaces.IUsuarioRepo;

import java.util.ArrayList;
import java.util.List;

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
    public UsuarioEntidad buscarPorId(Long idEntidad) {
        return usuariosEntidad.stream()
                .filter(u -> u.getIdUsuario().equals(idEntidad))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<UsuarioEntidad> listarTodos() {
        return new ArrayList<>(usuariosEntidad);
    }

    @Override
    public UsuarioEntidad actualizar(Long idEntidad, UsuarioForm form) {
        UsuarioEntidad usuario = buscarPorId(idEntidad);
        if (usuario == null) return null;

        UsuarioEntidad nuevoUsuario = UsuarioFormularioAEntidadMapper.actualizarUsuarioEntidad(idEntidad, form);
        usuariosEntidad.remove(usuario);
        usuariosEntidad.add(nuevoUsuario);

        return nuevoUsuario;
    }

    @Override
    public boolean eliminar(Long idEntidad) {
        UsuarioEntidad usuarioEntidad = buscarPorId(idEntidad);
        if (usuarioEntidad == null) {
            return false;
        }
        return usuariosEntidad.removeIf(u -> u.getIdUsuario().equals(idEntidad));
    }

    @Override
    public UsuarioEntidad buscarPorEmail(String email) {
        return usuariosEntidad.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public UsuarioEntidad buscarPorNombreUsuario(String nombreUsuario) {
        return usuariosEntidad.stream()
                .filter(u -> u.getNombreUsuario().equalsIgnoreCase(nombreUsuario))
                .findFirst()
                .orElse(null);
    }
}
