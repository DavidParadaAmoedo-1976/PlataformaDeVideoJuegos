package org.davidparada;

import org.davidparada.controlador.UsuarioControlador;
import org.davidparada.modelo.formulario.validacion.JuegoFormValidador;
import org.davidparada.modelo.formulario.validacion.UsuarioFormValidador;
import org.davidparada.repositorio.implementacionMemoria.JuegoRepoMemoria;
import org.davidparada.repositorio.implementacionMemoria.UsuarioRepoMemoria;

public class Program {

    static void main(String[] args) {

        // Repositorios
        JuegoRepoMemoria juegoRepoMemoria = new JuegoRepoMemoria();
        UsuarioRepoMemoria usuarioRepoMemoria = new UsuarioRepoMemoria();

        // Inyección en validadores
        JuegoFormValidador.setJuegoRepo(juegoRepoMemoria);
        UsuarioFormValidador.setUsuarioRepo(usuarioRepoMemoria);

        // Controladores
        UsuarioControlador usuarioControlador =
                new UsuarioControlador(usuarioRepoMemoria);

    }

}

